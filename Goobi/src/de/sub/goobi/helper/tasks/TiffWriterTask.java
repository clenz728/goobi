package de.sub.goobi.helper.tasks;
/**
 * This file is part of the Goobi Application - a Workflow tool for the support of mass digitization.
 * 
 * Visit the websites for more information. 
 *     		- http://www.goobi.org
 *     		- http://launchpad.net/goobi-production
 * 		    - http://gdz.sub.uni-goettingen.de
 * 			- http://www.intranda.com
 * 			- http://digiverso.com 
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * Linking this library statically or dynamically with other modules is making a combined work based on this library. Thus, the terms and conditions
 * of the GNU General Public License cover the whole combination. As a special exception, the copyright holders of this library give you permission to
 * link this library with independent modules to produce an executable, regardless of the license terms of these independent modules, and to copy and
 * distribute the resulting executable under terms of your choice, provided that you also meet, for each linked independent module, the terms and
 * conditions of the license of that module. An independent module is a module which is not derived from or based on this library. If you modify this
 * library, you may extend this exception to your version of the library, but you are not obliged to do so. If you do not wish to do so, delete this
 * exception statement from your version.
 */

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.goobi.beans.Process;

import de.sub.goobi.helper.NIOFileUtils;

public class TiffWriterTask extends LongRunningTask {

   @Override
   public void initialize(Process inProzess) {
      super.initialize(inProzess);
      setTitle("Tiffwriter: " + inProzess.getTitel());
   }

   /**
    * Aufruf als Thread
    * ================================================================*/
   @Override
public void run() {
      setStatusProgress(2);
      String imageFolder = "";
      /* ---------------------
       * Imageordner ermitteln
       * -------------------*/
      try {
         imageFolder = getProzess().getImagesDirectory();
      } catch (Exception e) {
    	  logger.error(e);
         setStatusMessage("Error while getting process data folder: " + e.getClass().getName() + " - "
               + e.getMessage());
         setStatusProgress(-1);
         return;
      }
      if (imageFolder.equals("")) {
         setStatusMessage("No imagefolder found");
         setStatusProgress(-1);
         return;
      }

      List<Path> myTifs = new ArrayList<Path>();
      listAllTifFiles(Paths.get(imageFolder), myTifs);
      logger.trace(myTifs.size());

      int progressStepSizePerImage = 50 / myTifs.size();
      for (Path file : myTifs) {
         setStatusProgress(getStatusProgress() + progressStepSizePerImage);
         logger.trace(getStatusProgress() + ": " + file.toString());
      }

      /* ---------------------
       * Abschluss
       * -------------------*/
      setStatusMessage("done");
      setStatusProgress(100);
      
   }

   //TODO Make this public and move it to FileUtils
   // Process only files under dir
   private void listAllTifFiles(Path dir, List<Path> inFiles) {

       List<Path> folders = NIOFileUtils.listFiles(dir.toString(), NIOFileUtils.folderFilter);
       for (Path folder : folders) {
           listAllTifFiles(folder, inFiles);
       }

       List<Path> files = NIOFileUtils.listFiles(dir.toString(), NIOFileUtils.imageNameFilter);

       inFiles.addAll(files);

   }
}
