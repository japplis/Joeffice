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

import org.joeffice.wordprocessor.BorderAttributes;
import org.joeffice.wordprocessor.DocxDocument;

import javax.swing.*;
import javax.swing.text.BoxView;
import javax.swing.text.Element;
import javax.swing.text.View;
import java.awt.*;

/**
 * Represents view for a table cell element.
 *
 * @author	Stanislav Lapitsky
 */
public class CellView extends BoxView {

    /**
     * Constructs new cell view instance.
     *
     * @param elem the element this view is responsible for
     */
    public CellView(Element elem) {
        super(elem, View.Y_AXIS);
        DocxDocument.CellElement cell = (DocxDocument.CellElement) getElement();
        Insets margins = cell.getMargins();

        setInsets((short) (margins.top),
                (short) (margins.left),
                (short) (margins.bottom),
                (short) (margins.right));
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
            DocxDocument.CellElement cell = (DocxDocument.CellElement) getElement();
            span = cell.getWidth();
        } else {
            DocxDocument.CellElement cell = (DocxDocument.CellElement) getElement();
            span = Math.max(super.getPreferredSpan(axis), cell.getHeight());
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
            DocxDocument.CellElement cell = (DocxDocument.CellElement) getElement();
            span = cell.getWidth();
        } else {
            DocxDocument.CellElement cell = (DocxDocument.CellElement) getElement();
            span = Math.max(super.getMinimumSpan(axis), cell.getHeight());
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
            DocxDocument.CellElement cell = (DocxDocument.CellElement) getElement();
            span = cell.getWidth();
        } else {
            DocxDocument.CellElement cell = (DocxDocument.CellElement) getElement();
            span = Math.max(super.getMaximumSpan(axis), cell.getHeight());
        }
        return span;
    }

    /**
     * Determines base line requirement along axis.
     *
     * @param axis may be either View.X_AXIS or View.Y_AXIS
     * @param	r Size requirements
     * @return A base line requirement.
     */
    @Override
    protected SizeRequirements baselineRequirements(int axis, SizeRequirements r) {
        SizeRequirements sr = super.baselineRequirements(axis, r);
        if (axis == View.Y_AXIS) {
            sr.alignment = 0f;
        }
        return sr;
    }

    /**
     * Determines major requirement along axis.
     *
     * @param axis may be either View.X_AXIS or View.Y_AXIS
     * @param	r Size requirements
     * @return A major requirement.
     */
    @Override
    protected SizeRequirements calculateMajorAxisRequirements(int axis, SizeRequirements r) {
        SizeRequirements sr = super.calculateMajorAxisRequirements(axis, r);
        if (axis == View.Y_AXIS) {
            sr.alignment = 0f;
        }
        return sr;
    }

    /**
     * Determines minor requirement along axis.
     *
     * @param axis may be either View.X_AXIS or View.Y_AXIS
     * @param	r Size requirements
     * @return A minor requirement.
     */
    @Override
    protected SizeRequirements calculateMinorAxisRequirements(int axis, SizeRequirements r) {
        SizeRequirements sr = super.calculateMinorAxisRequirements(axis, r);
        if (axis == View.Y_AXIS) {
            sr.alignment = 0f;
        }
        return sr;
    }

    /**
     * Performs layout of cells and cell's margins.
     *
     * @param width - the width of cell.
     * @param height - the height of cell.
     */
    @Override
    protected void layout(int width, int height) {
        DocxDocument.CellElement cell = (DocxDocument.CellElement) getElement();
        Insets margins = cell.getMargins();
        setInsets((short) (margins.top),
                (short) (margins.left),
                (short) (margins.bottom),
                (short) (margins.right));
        super.layout(width, height);
    }

    /**
     * Renders using the given rendering surface and area on that surface. If page is printed then borders do not
     * renders.
     *
     * @param g the rendering surface to use
     * @param a the allocated region to render into
     */
    @Override
    public void paint(Graphics g, Shape a) {
        Rectangle alloc = (a instanceof Rectangle) ? (Rectangle) a : a.getBounds();
        super.paint(g, a);
        DocxDocument.CellElement cell = (DocxDocument.CellElement) getElement();
        BorderAttributes ba = (BorderAttributes) cell.getAttribute("BorderAttributes");

        Color oldColor = g.getColor();
        g.setColor(ba.lineColor);

        // --- DRAW LEFT ---
        if (ba.borderLeft != 0) {
            g.drawLine(alloc.x, alloc.y, alloc.x, alloc.y + alloc.height);
        }
        // --- DRAW RIGHT ---
        if (ba.borderRight != 0) {
            g.drawLine(alloc.x + alloc.width, alloc.y, alloc.x + alloc.width, alloc.y + alloc.height);
        }
        // --- DRAW TOP ---
        if (ba.borderTop != 0) {
            g.drawLine(alloc.x, alloc.y, alloc.x + alloc.width, alloc.y);
        }
        // --- DRAW BOTTOM ---
        if (ba.borderBottom != 0) {
            g.drawLine(alloc.x, alloc.y + alloc.height, alloc.x + alloc.width, alloc.y + alloc.height);
        }
        g.setColor(oldColor);
    }
}