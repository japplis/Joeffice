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
package org.joeffice.desktop.ui;

import static javax.swing.TransferHandler.COPY_OR_MOVE;

import java.awt.Image;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.TransferHandler;

/**
 * Generic utility methods for a TransferHandler.
 * 
 * @author Anthony Goubard - Japplis
 */
public class OfficeTransferHandler extends TransferHandler {

    public String getTextFromTransferable(Transferable t, DataFlavor flavor) {
        if (t.isDataFlavorSupported(flavor)) {
            try {
                Object data = t.getTransferData(flavor);
                if (data instanceof InputStream) {
                    String charsetName = flavor.getParameter("charset") == null ? "UTF-8" : flavor.getParameter("charset");
                    return readFully((InputStream) data, Charset.forName(charsetName));
                } else if (data instanceof Reader) {
                    return readFully((Reader) data);
                } else if (data instanceof String) {
                    return (String) data;
                }
            } catch (UnsupportedFlavorException | IOException ex) {
            }
        }
        return null;
    }

    public ImageIcon getImageFromTransferable(Transferable t) {
        if (t.isDataFlavorSupported(DataFlavor.imageFlavor)) {
            try {
                Image image = (Image) t.getTransferData(DataFlavor.imageFlavor);
                return new ImageIcon(image);
            } catch (UnsupportedFlavorException | IOException ex) {
            }
        }
        return null;
    }

    private String readFully(Reader reader) throws IOException {
        char[] buffer = new char[1024];
        StringBuilder dataAsString = new StringBuilder();
        int length = reader.read(buffer);
        while (length != -1) {
            dataAsString.append(buffer, 0, length);
            length = reader.read(buffer);
        }
        return dataAsString.toString();
    }

    private String readFully(InputStream stream, Charset charset) throws IOException {
        byte[] buffer = new byte[stream.available()];
        ByteArrayOutputStream allData = new ByteArrayOutputStream();
        int length = stream.read(buffer);
        while (length != -1) {
            allData.write(buffer, 0, length);
            length = stream.read(buffer);
        }
        return new String(allData.toByteArray(), charset);
    }

    @Override
    public void exportToClipboard(JComponent comp, Clipboard clip, int action) {
        Transferable content = createTransferable(comp);
        clip.setContents(content, null);
        exportDone(comp, content, action);
    }

    @Override
    public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
    }

    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {
        return true;
    }
}
