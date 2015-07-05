/*
 * Copyright (c) 2015 Laszlo Balazs-Csiki
 *
 * This file is part of Pixelitor. Pixelitor is free software: you
 * can redistribute it and/or modify it under the terms of the GNU
 * General Public License, version 3 as published by the Free
 * Software Foundation.
 *
 * Pixelitor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Pixelitor. If not, see <http://www.gnu.org/licenses/>.
 */
package pixelitor.layers;

import pixelitor.AppLogic;
import pixelitor.Composition;
import pixelitor.ImageComponent;
import pixelitor.ImageComponents;
import pixelitor.utils.IconUtils;
import pixelitor.utils.ImageSwitchListener;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * An Action that adds a new layer mask.
 */
public class AddNewLayerMaskAction extends AbstractAction implements ImageSwitchListener, LayerMaskChangeListener {
    public static final AddNewLayerMaskAction INSTANCE = new AddNewLayerMaskAction();

    private AddNewLayerMaskAction() {
        super("Add New Layer Mask", IconUtils.loadIcon("add_layer_mask.png"));
        setEnabled(false);
        ImageComponents.addImageSwitchListener(this);
        AppLogic.addLayerMaskChangeListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Composition comp = ImageComponents.getActiveComp().get();
        Layer layer = comp.getActiveLayer();
        assert !layer.hasMask();
        boolean ctrlPressed = ((e.getModifiers() & ActionEvent.CTRL_MASK) == ActionEvent.CTRL_MASK);
        if (comp.hasSelection()) {
            if (ctrlPressed) {
                layer.addMask(LayerMaskAddType.HIDE_SELECTION);
            } else {
                layer.addMask(LayerMaskAddType.REVEAL_SELECTION);
            }
        } else { // there is no selection
            if (ctrlPressed) {
                layer.addMask(LayerMaskAddType.HIDE_ALL);
            } else {
                layer.addMask(LayerMaskAddType.REVEAL_ALL);
            }
        }
    }

    @Override
    public void noOpenImageAnymore() {
        setEnabled(false);
    }

    @Override
    public void newImageOpened(Composition comp) {
        setEnabled(!comp.getActiveLayer().hasMask());
    }

    @Override
    public void activeImageHasChanged(ImageComponent oldIC, ImageComponent newIC) {
        boolean hasMask = newIC.getComp().getActiveLayer().hasMask();
        setEnabled(!hasMask);
    }

    @Override
    public void maskAddedOrRemoved(Layer affectedLayer) {
        setEnabled(!affectedLayer.hasMask());
    }
}