package kfupm.clinic.service;

public class NaiveMatcher implements StringMatcher {
    @Override
    public boolean contains(String text, String pattern) {
        // TODO: implement naive substring search
        if (text == null || pattern == null) {
            return false;
        }
        if (pattern.length() == 0) {
            return true;
        }
        for (int i = 0; i <= text.length() - pattern.length(); i++) {
            int j = 0;
            while (j < pattern.length() && text.charAt(i + j) == pattern.charAt(j)) {
                j++;
            }
            if (j == pattern.length()) {
                return true;
            }
        }
        return false;
    }
}