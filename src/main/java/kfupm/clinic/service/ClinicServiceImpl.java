package kfupm.clinic.service;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import kfupm.clinic.api.Result;
import kfupm.clinic.ds.*;
import kfupm.clinic.model.*;
import kfupm.clinic.matching.*;

/**
 * Students implement the system logic here.
 *
 * Rules:
 * - Use the provided custom data structures.
 * - Do NOT use Java built-in maps/trees/priority queues for storage.
 */
public class ClinicServiceImpl implements ClinicService {

    // Hash tables
    private final HashTable<String, Patient> patientsById = new HashTable<>();
    private final HashTable<String, Appointment> apptsById = new HashTable<>();

    // Appointment schedule index (AVL)
    private final AVLTree<AppointmentKey, Appointment> apptsByTime = new AVLTree<>();

    // Walk-ins and urgent
    private final LinkedQueue<Patient> walkIns = new LinkedQueue<>();
    private final MaxHeap<UrgentPatient> urgentHeap = new MaxHeap<>((a, b) -> {
        // Higher severity first; tie-break earlier arrival first.
        if (a.severity() != b.severity()) return Integer.compare(a.severity(), b.severity());
        // earlier arrival should win => invert compare so earlier is "greater"
        return Long.compare(b.arrivalEpochMillis(), a.arrivalEpochMillis());
    });

    // Undo + log
    private final LinkedStack<Action> undo = new LinkedStack<>();
    private final SinglyLinkedList<VisitLogEntry> log = new SinglyLinkedList<>();

    private final StringMatcher naive = new NaiveMatcher();
    private final StringMatcher kmp = new KMPMatcher();

    private int nextApptId = 1;

    @Override
    public Result<Void> addPatient(String id, String name, String phone) {
        if (patientsById.get(id) != null) {
            return Result.fail("Patient already exists.");
        }

        Patient patient = new Patient(id, name, phone);
        patientsById.put(id, patient);
        undo.push(new Action(ActionType.ADD_PATIENT, patient));

        return Result.ok(null, "Patient added.");
    }

    @Override
    public Result<Patient> findPatient(String id) {
        Patient patient = patientsById.get(id);

        if (patient == null) {
            return Result.fail("Patient not found.");
        }

        return Result.ok(patient, "Patient found.");
    }

    @Override
    public Result<Void> deletePatient(String id) {
        Patient patient = patientsById.remove(id);

        if (patient == null) {
            return Result.fail("Patient not found.");
        }

        undo.push(new Action(ActionType.DELETE_PATIENT, patient));
        return Result.ok(null, "Patient deleted.");
    }

    @Override
    public Result<String> addAppointment(String patientId, LocalDate date, LocalTime time, String doctor) {
        Patient patient = patientsById.get(patientId);

        if (patient == null) {
            return Result.fail("Patient not found.");
        }

        String appointmentId = newAppointmentId();

        Appointment appointment = new Appointment(appointmentId, patient.id(), patient.name(),
                patient.phone(), date, time, doctor);

        AppointmentKey key = new AppointmentKey(date, time, appointmentId);

        apptsById.put(appointmentId, appointment);
        apptsByTime.put(key, appointment);
        undo.push(new Action(ActionType.ADD_APPT, appointment));

        return Result.ok(appointmentId, "Appointment added.");
    }

    @Override
    public Result<Void> cancelAppointment(String appointmentId) {
        Appointment appointment = apptsById.remove(appointmentId);

        if (appointment == null) {
            return Result.fail("Appointment not found.");
        }

        AppointmentKey key = new AppointmentKey(appointment.date(), appointment.time(), appointment.appointmentId());
        apptsByTime.remove(key);

        undo.push(new Action(ActionType.CANCEL_APPT, appointment));
        return Result.ok(null, "Appointment cancelled.");
    }

    @Override
    public Result<Appointment> findAppointment(String appointmentId) {
        Appointment appointment = apptsById.get(appointmentId);

        if (appointment == null) {
            return Result.fail("Appointment not found.");
        }

        return Result.ok(appointment, "Appointment found.");
    }

    @Override
    public List<Appointment> viewDay(LocalDate date) {
        List<Appointment> result = new ArrayList<>();

        apptsByTime.inOrder((key, appointment) -> {
            if (appointment.date().equals(date)) {
                result.add(appointment);
            }
        });

        return result;
    }

    @Override
    public List<Appointment> viewRange(LocalDate date, LocalTime start, LocalTime end) {
        List<Appointment> result = new ArrayList<>();

        apptsByTime.inOrder((key, appointment) -> {
            if (appointment.date().equals(date) &&
                    !appointment.time().isBefore(start) &&
                    !appointment.time().isAfter(end)) {
                result.add(appointment);
            }
        });

        return result;
    }

    @Override
    public Result<Void> addWalkIn(String patientId) {
        Patient patient = patientsById.get(patientId);

        if (patient == null) {
            return Result.fail("Patient not found.");
        }

        walkIns.enqueue(patient);
        undo.push(new Action(ActionType.ADD_WALKIN, patient));

        return Result.ok(null, "Walk-in added.");
    }

    @Override
    public List<Patient> viewWalkIns() {
        // Non-destructive view
        return walkIns.toList();
    }

    @Override
    public Result<Void> addUrgent(String patientId, int severity) {
        Patient patient = patientsById.get(patientId);

        if (patient == null) {
            return Result.fail("Patient not found.");
        }

        UrgentPatient urgentPatient = new UrgentPatient(patient, severity, System.currentTimeMillis());
        urgentHeap.push(urgentPatient);
        undo.push(new Action(ActionType.ADD_URGENT, urgentPatient));

        return Result.ok(null, "Urgent patient added.");
    }

    @Override
    public Result<UrgentPatient> peekUrgent() {
        UrgentPatient up = urgentHeap.peek();
        if (up == null) return Result.fail("No urgent patients.");
        return Result.ok(up, "Most urgent patient.");
    }

    @Override
    public List<UrgentPatient> viewUrgentsSnapshot() {
        return urgentHeap.toListSnapshot();
    }

    @Override
    public Result<VisitLogEntry> serveNext(String doctor, String note) {
        Patient patient;
        String type;

        UrgentPatient urgentPatient = urgentHeap.pop();

        if (urgentPatient != null) {
            patient = urgentPatient.patient();
            type = "URGENT";
        } else if (!walkIns.isEmpty()) {
            patient = walkIns.dequeue();
            type = "WALKIN";
        } else {
            AVLTree.Entry<AppointmentKey, Appointment> entry = apptsByTime.minEntry();

            if (entry == null) {
                return Result.fail("No patients to serve.");
            }

            Appointment appointment = entry.value();
            patient = new Patient(appointment.patientId(), appointment.patientName(), appointment.phone());
            type = "APPOINTMENT";

            apptsByTime.remove(entry.key());
            apptsById.remove(appointment.appointmentId());
        }

        VisitLogEntry visit = new VisitLogEntry(System.currentTimeMillis(),
                patient.id(), patient.name(), type, doctor, note);

        log.addLast(visit);
        undo.push(new Action(ActionType.SERVE, visit));

        return Result.ok(visit, "Patient served.");
    }

    @Override
    public List<VisitLogEntry> printLog() {
        return log.toList();
    }

    @Override
    public List<VisitLogEntry> searchLogNaive(String pattern) {
        List<VisitLogEntry> result = new ArrayList<>();

        for (VisitLogEntry entry : log.toList()) {
            if (entry.notes() != null && naive.contains(entry.notes(), pattern)) {
                result.add(entry);
            }
        }

        return result;
    }

    @Override
    public List<VisitLogEntry> searchLogKmp(String pattern) {
        List<VisitLogEntry> result = new ArrayList<>();

        for (VisitLogEntry entry : log.toList()) {
            if (entry.notes() != null && kmp.contains(entry.notes(), pattern)) {
                result.add(entry);
            }
        }

        return result;
    }

    @Override
    public Result<Action> undo() {
        Action action = undo.pop();

        if (action == null) {
            return Result.fail("Nothing to undo.");
        }

        return Result.ok(action, "Undo completed.");
    }

    // Helpers you may want
    private String newAppointmentId() {
        return "A" + (nextApptId++);
    }
}