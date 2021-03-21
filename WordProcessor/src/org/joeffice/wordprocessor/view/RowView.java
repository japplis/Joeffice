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
package org.joeffice.wordprocessor.view;

import org.joeffice.wordprocessor.DocxDocument;

import javax.swing.text.BoxView;
import javax.swing.text.Element;
import javax.swing.text.View;
import java.awt.*;

/**
 * Represens view for table's row.
 *
 * @author	Stanislav Lapitsky
 */
public class RowView extends BoxView {

    /**
     * Constructs new view instance.
     *
     * @param	elem The parent row element.
     * @param	axis either View.X_AXIS or View.Y_AXIS
     */
    public RowView(Element elem, int axis) {
        super(elem, axis);
    }

    /**
     * Constructs new view instance.
     *
     * @param	elem The parent row element.
     */
    public RowView(Element elem) {
        super(elem, View.X_AXIS);
    }

    /**
     * Renders using the given rendering surface and area on that surface.
     *
     * @param g the rendering surface to use
     * @param a the allocated region to render into
     */
    @Override
    public void paint(Graphics g, Shape a) {
        Rectangle alloc = (a instanceof Rectangle) ? (Rectangle) a : a.getBounds();
        int n = getViewCount();
        DocxDocument.RowElement row = (DocxDocument.RowElement) getElement();
        int cellWidth = (row.getWidth() / row.getChildCount());
        int shift = 0;
        for (int i = 0; i < n; i++) {
            Rectangle tempRect = new Rectangle(alloc.x + shift, alloc.y, row.getCellWidth(i), alloc.height);
            paintChild(g, tempRect, i);
            shift += row.getCellWidth(i);
        }
    }

    /**
     * Determines the preferred span for this view along an axis.
     *
     * @param axis may be either View.X_AXIS or View.Y_AXIS
     * @returns the span the view would like to be rendered into >= 0. Typically the view is told to render into the
     * span that is returned, although there is no guarantee. The parent may choose to resize or break the view.
     */
    @Override
    public float getPreferredSpan(int axis) {
        float span = 0;
        if (axis == View.X_AXIS) {
            DocxDocument.RowElement row = (DocxDocument.RowElement) getElement();
            span = row.getWidth();
        } else {
            span = 1;
            for (int i = 0; i < getViewCount(); i++) {
                span = Math.max(span, getView(i).getPreferredSpan(axis));
            }
        }
        return span;
    }

    /**
     * Determines the minimum span for this view along an axis.
     *
     * @param axis may be either View.X_AXIS or View.Y_AXIS
     * @returns the span the view would like to be rendered into >= 0. Typically the view is told to render into the
     * span that is returned, although there is no guarantee. The parent may choose to resize or break the view.
     */
    @Override
    public float getMinimumSpan(int axis) {
        float span = 0;
        if (axis == View.X_AXIS) {
            DocxDocument.RowElement row = (DocxDocument.RowElement) getElement();
            span = row.getWidth();
        } else {
            span = 1;
            for (int i = 0; i < getViewCount(); i++) {
                span = Math.max(span, getView(i).getMinimumSpan(axis));
            }
        }
        return span;
    }

    /**
     * Determines the maximum span for this view along an axis.
     *
     * @param axis may be either View.X_AXIS or View.Y_AXIS
     * @returns the span the view would like to be rendered into >= 0. Typically the view is told to render into the
     * span that is returned, although there is no guarantee. The parent may choose to resize or break the view.
     */
    @Override
    public float getMaximumSpan(int axis) {
        float span = 0;
        if (axis == View.X_AXIS) {
            DocxDocument.RowElement row = (DocxDocument.RowElement) getElement();
            span = row.getWidth();
        } else {
            span = 1;
            for (int i = 0; i < getViewCount(); i++) {
                span = Math.max(span, getView(i).getMaximumSpan(axis));
            }
        }
        return span;
    }

    /**
     * Paints a child. By default that is all it does, but a subclass can use this to paint things relative to the
     * child.
     *
     * @param g the graphics context
     * @param alloc the allocated region to paint into
     * @param index the child index, >= 0 && < getViewCount()
     */
    @Override
    protected void paintChild(Graphics g, Rectangle alloc, int index) {
        View child = getView(index);
        child.paint(g, alloc);
    }
}