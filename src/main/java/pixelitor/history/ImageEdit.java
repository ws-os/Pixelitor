/*
 * Copyright 2017 Laszlo Balazs-Csiki
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

package pixelitor.history;

import pixelitor.Composition;
import pixelitor.layers.Drawable;
import pixelitor.selection.IgnoreSelection;
import pixelitor.utils.debug.DebugNode;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import java.awt.image.BufferedImage;
import java.lang.ref.SoftReference;

import static pixelitor.Composition.ImageChangeActions.FULL;

/**
 * A PixelitorEdit that represents the changes made to an image.
 */
public class ImageEdit extends FadeableEdit {
    private final IgnoreSelection ignoreSelection;
    private SoftReference<BufferedImage> imgRef;
    protected Drawable dr;

    private final boolean canRepeat;

    public ImageEdit(Composition comp, String name, Drawable dr,
                     BufferedImage backupImage,
                     IgnoreSelection ignoreSelection, boolean canRepeat) {
        super(comp, dr, name);
        this.ignoreSelection = ignoreSelection;

        assert dr != null;
        assert backupImage != null;

        // the backup image is stored in an SoftReference
        this.imgRef = new SoftReference<>(backupImage);
        this.dr = dr;
        this.canRepeat = canRepeat;

        checkBackupDifferentFromActive();
    }

    private void checkBackupDifferentFromActive() {
        // the backup should never be identical to the active image
        // otherwise the backup might be also edited
        BufferedImage layerImage = dr.getImage();
        if (layerImage == imgRef.get()) {
            throw new IllegalStateException("backup BufferedImage is identical to the active one");
        }
    }

    @Override
    public void undo() throws CannotUndoException {
        super.undo();

        if (!swapImages()) {
            throw new CannotUndoException();
        }
    }

    @Override
    public void redo() throws CannotRedoException {
        super.redo();

        if (!swapImages()) {
            throw new CannotRedoException();
        }
    }

    /**
     * Returns true if successful
     */
    private boolean swapImages()  {
        BufferedImage backupImage = imgRef.get();
        if(backupImage == null) {
            return false;
        }

        BufferedImage tmp;
        if(ignoreSelection.isYes()) {
            tmp = dr.getImage();
        } else {
            tmp = dr.getImageOrSubImageIfSelected(false, true);
        }
        dr.changeImageUndoRedo(backupImage, ignoreSelection);

        // create new backup image from tmp
        imgRef = new SoftReference<>(tmp);

        if(!embedded) {
            comp.imageChanged(FULL);
            dr.updateIconImage();
            History.notifyMenus(this);
        }

        checkBackupDifferentFromActive();
        return true;
    }

    @Override
    public void die() {
        super.die();

        BufferedImage backupImage = imgRef.get();
        if(backupImage != null) {
            backupImage.flush();
        }

        imgRef = null;
        dr = null;
    }

    @Override
    public BufferedImage getBackupImage() {
        if(imgRef != null) {
            // this still could be null
            return imgRef.get();
        }
        return null;
    }

    @Override
    public boolean canRepeat() {
        return canRepeat;
    }

    @Override
    public DebugNode getDebugNode() {
        DebugNode node = super.getDebugNode();

        BufferedImage img = imgRef.get();
        if (img != null) {
            node.addIntChild("Backup Image Width", img.getWidth());
            node.addIntChild("Backup Image Height", img.getHeight());
        }

        return node;
    }

}
