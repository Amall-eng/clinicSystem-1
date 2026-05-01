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

        int[] lps = LPSarray(pattern);
        int i = 0;
        int j = 0;
        while (i < text.length()){
            if(text.charAt(i) == pattern.charAt(j)){
                i++;
                j++;

                if (j == pattern.length()){
                    return true;
                }
                else{
                    if(j != 0){
                        j = lps[j-1];
                    }
                    else{
                        i++;
                    }
                }
            }
            return false;
        }

    }

    // TODO: implement prefix function / LPS array helper
    private int[] LPSarray(String pattern){
        int[] lps = new int[pattern.length()];
        int i = 0;
        int j = 0;
        while (i < pattern.length()) {
            if (pattern.charAt(i) == pattern.charAt(j)) {
                j++;
                lps[i] = j;
                i++;
            }
            else {
                 if (j == 0){
                     j = lps[j-1];
                 }
                 else{
                     lps[i] = 0;
                     i++;
                 }
              }
        }
        return lps;
    }
}