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
package org.joeffice.wordprocessor.app;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Represents margins object (top, bottom, left and right margins).
 *
 * @author Stanislav Lapitsky
 */
public class MarginsControl extends JPanel {

    public DoubleSpinEdit marginLeft=new DoubleSpinEdit(0,20,2);
    public DoubleSpinEdit marginRight=new DoubleSpinEdit(0,20,2);
    public DoubleSpinEdit marginTop=new DoubleSpinEdit(0,20,2);
    public DoubleSpinEdit marginBottom=new DoubleSpinEdit(0,20,2);

    /**
     * Constructs new instance of control.
     */
    public MarginsControl() {
        super();
        setBorder(new TitledBorder(new EtchedBorder(), "Margins:"));

        setLayout(new GridBagLayout());
        add(new JLabel("Left:"),new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHEAST,GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0));
        add(new JLabel("Right:"),new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHEAST,GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0));
        add(new JLabel("Top:"),new GridBagConstraints(0,2,1,1,0,0,GridBagConstraints.NORTHEAST,GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0));
        add(new JLabel("Bottom:"),new GridBagConstraints(0,3,1,1,0,0,GridBagConstraints.NORTHEAST,GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0));

        add(marginLeft,new GridBagConstraints(1,0,1,1,1,0,GridBagConstraints.NORTHEAST,GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0));
        add(marginRight,new GridBagConstraints(1,1,1,1,1,0,GridBagConstraints.NORTHEAST,GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0));
        add(marginTop,new GridBagConstraints(1,2,1,1,1,0,GridBagConstraints.NORTHEAST,GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0));
        add(marginBottom,new GridBagConstraints(1,3,1,1,1,0,GridBagConstraints.NORTHEAST,GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0));
    }

    /**
     * Constructs new instance of control with specified margins values.
     *
     * @param margins magrins values.
     */
    public MarginsControl(Insets margins) {
        this();
        setMargins(margins);
    }

    /**
     * Sets margins to specified value.
     *
     * @param margins margin values.
     */
    public void setMargins(Insets margins) {
        marginLeft.setValue(margins.left);
        marginRight.setValue(margins.right);
        marginTop.setValue(margins.top);
        marginBottom.setValue(margins.bottom);
    }

    /**
     * Gets user's choice. Creates and returns Insets object according to
     * user's selection.
     *
     * @return
     */
    public Insets getMargins() {
        Insets margins=new Insets((int)marginTop.getValue(),(int)marginLeft.getValue(),(int)marginBottom.getValue(),(int)marginRight.getValue());
        return margins;
    }
}