package kfupm.clinic.service;

public class KMPMatcher implements StringMatcher {
    @Override
    public boolean contains(String text, String pattern) {

        // Empty pattern
        if (pattern == null || pattern.length() == 0) {
            return true;
        }

        //Empty text
        if (text == null || text.length() == 0) {
            return false;
        }

        // Case-insensitive
        text = text.toLowerCase();
        pattern = pattern.toLowerCase();

        return false;

    }

    // TODO: implement prefix function / LPS array helper
}