package de.dragon.FTClient.misc;

import de.dragon.FTClient.frame.FTPFrame;
import de.dragon.FTClient.ftpnet.Upload;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class DropListener extends DropTarget {

	private Upload uploader;

	public DropListener(Upload uploader) {
		this.uploader = uploader;
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized void drop(DropTargetDropEvent arg0) {
		arg0.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
		try {
			List<File> fileList = (List<File>) arg0.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
		    for(File c : fileList) {
		    	uploader.addToQueue(c);
		    }
		} catch (UnsupportedFlavorException | IOException e1) {
			e1.printStackTrace();
		}
		arg0.dropComplete(true);
	}

}
