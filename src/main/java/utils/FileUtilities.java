package utils;

import java.io.File;

public class FileUtilities {

    public boolean verifyFilePresence(String fileDirectory) { //Verifies presence of a file at a given directory

        boolean fileIsPresent = false;

        try {
            File file = new File(fileDirectory);

            fileIsPresent = file.exists();

        } catch (Exception gamma) {

            gamma.printStackTrace();

        }
        return fileIsPresent;
    }
}
