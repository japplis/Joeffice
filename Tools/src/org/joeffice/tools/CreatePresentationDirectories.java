/*
 * Copyright 2013 Japplis.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.joeffice.tools;

import java.io.*;

/**
 * This class will create the directories for the 30 days development. In each directory, it will create a
 * presentation.txt file C:\Java\projects\Joeffice\tools\src>javac org\joeffice\tools\CreatePresentationDirectories.java
 * java -cp . org.joeffice.tools.CreatePresentationDirectories
 *
 * @author Anthony Goubard
 */
public class CreatePresentationDirectories {

    public static void main(String[] args) throws IOException {
        File presentationsDir = new File("C:\\Java\\projects\\Joeffice\\admin\\marketing\\presentations");
        for (int i = 1; i <= 30; i++) {
            File dayDir = new File(presentationsDir, "day-" + i);
            dayDir.mkdir();
            File presentationFile = new File(dayDir, "presentation.txt");
            presentationFile.createNewFile();
        }
    }
}
