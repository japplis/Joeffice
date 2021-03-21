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

import java.util.ArrayList;
import javax.swing.event.UndoableEditEvent;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;
import org.openide.awt.UndoRedo;
import org.openide.util.Exceptions;

/**
 * UndoManager that bundles key typed together to avoid undo letter by letter.
 * Doesn't work with the Netbeans undo/redo manager
 *
 * @author Stanislav Lapitsky
 * @see http://java-sl.com/tip_merge_undo_edits.html
 */
public class TextUndoManager extends UndoRedo.Manager {

    private String lastEditName;
    private ArrayList<MyCompoundEdit> edits = new ArrayList<>();
    private MyCompoundEdit current;
    private int pointer = -1;

    @Override
    public void undoableEditHappened(UndoableEditEvent uev) {
        UndoableEdit edit = uev.getEdit();
        try {
            String text = null;
            if (edit instanceof AbstractDocument.DefaultDocumentEvent) { // Doesn't work since Java 9
                AbstractDocument.DefaultDocumentEvent event = (AbstractDocument.DefaultDocumentEvent) edit;
                int start = event.getOffset();
                int len = event.getLength();
                text = event.getDocument().getText(start, len);
            } else {
                text = "";
            }
            boolean isNeedStart = false;
            if (current == null) {
                isNeedStart = true;
            } else if (text.contains("\n")) {
                isNeedStart = true;
            } else if (lastEditName == null || !lastEditName.equals(edit.getPresentationName())) {
                isNeedStart = true;
            }

            while (pointer < edits.size() - 1) {
                edits.remove(edits.size() - 1);
                isNeedStart = true;
            }
            if (isNeedStart) {
                createCompoundEdit();
            }

            current.addEdit(edit);
            super.undoableEditHappened(uev);
            lastEditName = edit.getPresentationName();
        } catch (BadLocationException e1) {
            Exceptions.printStackTrace(e1);
        }
    }

    public void createCompoundEdit() {
        if (current == null || current.getLength() > 0) {
            current = new MyCompoundEdit();
        }

        edits.add(current);
        pointer++;
    }

    @Override
    public void undo() throws CannotUndoException {
        if (!canUndo()) {
            throw new CannotUndoException();
        }

        MyCompoundEdit u = edits.get(pointer);
        u.undo();
        pointer--;
    }

    @Override
    public void redo() throws CannotUndoException {
        if (!canRedo()) {
            throw new CannotUndoException();
        }

        pointer++;
        MyCompoundEdit u = edits.get(pointer);
        u.redo();
    }

    @Override
    public boolean canUndo() {
        return pointer >= 0;
    }

    @Override
    public boolean canRedo() {
        return edits.size() > 0 && pointer < edits.size() - 1;
    }

    class MyCompoundEdit extends CompoundEdit {
        boolean isUnDone=false;
        public int getLength() {
            return edits.size();
        }

        @Override
        public void undo() throws CannotUndoException {
            super.undo();
            isUnDone=true;
        }
        @Override
        public void redo() throws CannotUndoException {
            super.redo();
            isUnDone=false;
        }
        @Override
        public boolean canUndo() {
            return edits.size()>0 && !isUnDone;
        }

        @Override
        public boolean canRedo() {
            return edits.size()>0 && isUnDone;
        }
    }
}