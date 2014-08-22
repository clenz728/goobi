package de.sub.goobi.metadaten;

/***************************************************************
 * Copyright notice
 *
 * (c) 2013 Robert Sehr <robert.sehr@intranda.com>
 *
 * All rights reserved
 *
 * This file is part of the Goobi project. The Goobi project is free software;
 * you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * The GNU General Public License can be found at
 * http://www.gnu.org/copyleft/gpl.html.
 *
 * This script is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * This copyright notice MUST APPEAR in all copies of this file!
 ***************************************************************/

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.myfaces.custom.fileupload.UploadedFile;

import ugh.dl.ContentFile;
import ugh.dl.DigitalDocument;
import ugh.dl.DocStruct;
import ugh.dl.DocStructType;
import ugh.dl.Metadata;
import ugh.dl.MetadataType;
import ugh.dl.Prefs;
import ugh.exceptions.MetadataTypeNotAllowedException;
import ugh.exceptions.TypeNotAllowedForParentException;
import de.schlichtherle.io.FileInputStream;
import de.sub.goobi.config.ConfigurationHelper;
import de.sub.goobi.helper.FacesContextHelper;
import de.sub.goobi.helper.Helper;
import de.sub.goobi.helper.exceptions.DAOException;
import de.sub.goobi.helper.exceptions.SwapException;

import org.goobi.beans.Process;

public class FileManipulation {
    private static final Logger logger = Logger.getLogger(FileManipulation.class);
    private Metadaten metadataBean;

    public FileManipulation(Metadaten metadataBean) {
        this.metadataBean = metadataBean;
    }

    // insert new file after this page
    private String insertPage = "";

    private String imageSelection = "";

    // mode of insert (uncounted or into pagination sequence)
    private String insertMode = "uncounted";

    private UploadedFile uploadedFile = null;

    private String uploadedFileName = null;

    private List<String> selectedFiles = new ArrayList<String>();

    private boolean deleteFilesAfterMove = false;

    private boolean moveFilesInAllFolder = true;

    private List<String> allImportFolder = new ArrayList<String>();

    private String currentFolder = "";

    /**
     * File upload with binary copying.
     */
    public void uploadFile() {
        ByteArrayInputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            if (this.uploadedFile == null) {
                Helper.setFehlerMeldung("noFileSelected");
                return;
            }

            String basename = this.uploadedFile.getName();
            if (basename.startsWith(".")) {
                basename = basename.substring(1);
            }
            if (basename.contains("/")) {
                basename = basename.substring(basename.lastIndexOf("/") + 1);
            }
            if (basename.contains("\\")) {
                basename = basename.substring(basename.lastIndexOf("\\") + 1);
            }

            if (StringUtils.isNotBlank(uploadedFileName)) {

                String fileExtension = Metadaten.getFileExtension(basename);
                if (!fileExtension.isEmpty() && !uploadedFileName.endsWith(fileExtension)) {
                    uploadedFileName = uploadedFileName + fileExtension;
                }
                basename = uploadedFileName;
            }
            basename = basename.replace("[^\\p{ASCII}]", "_");
            basename = basename.replace("[\\:\\*\\?\\|\\/]", "_").replace(" ", "_");
            logger.trace("folder to import: " + currentFolder);
            String filename = metadataBean.getMyProzess().getImagesDirectory() + currentFolder + File.separator + basename;

            logger.trace("filename to import: " + filename);

            if (new File(filename).exists()) {
                List<String> parameterList = new ArrayList<String>();
                parameterList.add(basename);
                Helper.setFehlerMeldung(Helper.getTranslation("fileExists", parameterList));
                return;
            }

            inputStream = new ByteArrayInputStream(this.uploadedFile.getBytes());
            outputStream = new FileOutputStream(filename);

            byte[] buf = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, len);
            }
            logger.trace(filename + " was imported");
            // if file was uploaded into media folder, update pagination sequence
            if (metadataBean.getMyProzess().getImagesTifDirectory(false).equals(
                    metadataBean.getMyProzess().getImagesDirectory() + currentFolder + File.separator)) {
                logger.trace("update pagination for " + metadataBean.getMyProzess().getTitel());
                updatePagination(filename);

            }

            Helper.setMeldung(Helper.getTranslation("metsEditorFileUploadSuccessful"));
        } catch (IOException | SwapException | DAOException | InterruptedException | TypeNotAllowedForParentException
                | MetadataTypeNotAllowedException e) {
            logger.error(e.getMessage(), e);
            Helper.setFehlerMeldung("uploadFailed", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        metadataBean.retrieveAllImages();
        metadataBean.BildErmitteln(0);
    }

    public String getUploadedFileName() {
        return uploadedFileName;
    }

    public void setUploadedFileName(String uploadedFileName) {
        this.uploadedFileName = uploadedFileName;
    }

    private void updatePagination(String filename) throws TypeNotAllowedForParentException, IOException, InterruptedException, SwapException,
            DAOException, MetadataTypeNotAllowedException {
        if (!matchesFileConfiguration(filename)) {
            return;
        }

        if (insertPage.equals("lastPage")) {
            metadataBean.createPagination();
        } else {

            Prefs prefs = metadataBean.getMyProzess().getRegelsatz().getPreferences();
            DigitalDocument doc = metadataBean.getDocument();
            DocStruct physical = doc.getPhysicalDocStruct();

            List<DocStruct> pageList = physical.getAllChildren();

            int indexToImport = Integer.parseInt(insertPage);
            DocStructType newPageType = prefs.getDocStrctTypeByName("page");
            DocStruct newPage = doc.createDocStruct(newPageType);
            MetadataType physicalPageNoType = prefs.getMetadataTypeByName("physPageNumber");
            MetadataType logicalPageNoType = prefs.getMetadataTypeByName("logicalPageNumber");
            for (int index = 0; index < pageList.size(); index++) {

                if (index == indexToImport) {
                    DocStruct oldPage = pageList.get(index);

                    // physical page no for new page

                    Metadata mdTemp = new Metadata(physicalPageNoType);
                    mdTemp.setValue(String.valueOf(indexToImport + 1));
                    newPage.addMetadata(mdTemp);

                    // new physical page no for old page
                    oldPage.getAllMetadataByType(physicalPageNoType).get(0).setValue(String.valueOf(indexToImport + 2));

                    // logical page no
                    // logicalPageNoType = prefs.getMetadataTypeByName("logicalPageNumber");
                    mdTemp = new Metadata(logicalPageNoType);

                    if (insertMode.equalsIgnoreCase("uncounted")) {
                        mdTemp.setValue("uncounted");
                    } else {
                        // set new logical no. for new and old page 
                        Metadata oldPageNo = oldPage.getAllMetadataByType(logicalPageNoType).get(0);
                        mdTemp.setValue(oldPageNo.getValue());
                        if (index + 1 < pageList.size()) {
                            Metadata pageNoOfFollowingElement = pageList.get(index + 1).getAllMetadataByType(logicalPageNoType).get(0);
                            oldPageNo.setValue(pageNoOfFollowingElement.getValue());
                        } else {
                            oldPageNo.setValue("uncounted");
                        }
                    }

                    newPage.addMetadata(mdTemp);
                    doc.getLogicalDocStruct().addReferenceTo(newPage, "logical_physical");

                    ContentFile cf = new ContentFile();
                    cf.setLocation(filename);
                    newPage.addContentFile(cf);
                    doc.getFileSet().addFile(cf);

                }
                if (index > indexToImport) {
                    DocStruct currentPage = pageList.get(index);
                    // check if element is last element
                    currentPage.getAllMetadataByType(physicalPageNoType).get(0).setValue(String.valueOf(index + 2));
                    if (!insertMode.equalsIgnoreCase("uncounted")) {
                        if (index + 1 == pageList.size()) {
                            currentPage.getAllMetadataByType(logicalPageNoType).get(0).setValue("uncounted");
                        } else {
                            DocStruct followingPage = pageList.get(index + 1);
                            currentPage.getAllMetadataByType(logicalPageNoType).get(0).setValue(
                                    followingPage.getAllMetadataByType(logicalPageNoType).get(0).getValue());
                        }
                    }
                }
            }
            pageList.add(indexToImport, newPage);

        }
    }

    public UploadedFile getUploadedFile() {
        return this.uploadedFile;
    }

    public void setUploadedFile(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    public String getInsertPage() {
        return insertPage;
    }

    public void setInsertPage(String insertPage) {
        this.insertPage = insertPage;
    }

    public String getInsertMode() {
        return insertMode;
    }

    public void setInsertMode(String insertMode) {
        this.insertMode = insertMode;
    }

    /**
     * download file
     */

    public String getImageSelection() {
        return imageSelection;
    }

    public void setImageSelection(String imageSelection) {
        this.imageSelection = imageSelection;
    }

    public void downloadFile() {
        File downloadFile = null;

        int imageOrder = Integer.parseInt(imageSelection);
        DocStruct page = metadataBean.getDocument().getPhysicalDocStruct().getAllChildren().get(imageOrder);
        String imagename = page.getImageName();
        String filenamePrefix = imagename.substring(0, imagename.lastIndexOf("."));
        try {
            File[] filesInFolder = new File(metadataBean.getMyProzess().getImagesDirectory() + currentFolder).listFiles();
            for (File currentFile : filesInFolder) {
                String currentFileName = currentFile.getName();
                String currentFileNamePrefix = currentFileName.substring(0, currentFileName.lastIndexOf("."));
                if (filenamePrefix.equals(currentFileNamePrefix)) {
                    downloadFile = currentFile;
                    break;
                }
            }
        } catch (SwapException e1) {
            logger.error(e1);
        } catch (DAOException e1) {
            logger.error(e1);
        } catch (IOException e1) {
            logger.error(e1);
        } catch (InterruptedException e1) {
            logger.error(e1);
        }

        if (downloadFile == null || !downloadFile.exists()) {
            List<String> paramList = new ArrayList<String>();
            //           paramList.add(metadataBean.getMyProzess().getTitel());
            paramList.add(filenamePrefix);
            paramList.add(currentFolder);
            Helper.setFehlerMeldung(Helper.getTranslation("MetsEditorMissingFile", paramList));
            return;
        }

        FacesContext facesContext = FacesContextHelper.getCurrentFacesContext();
        if (!facesContext.getResponseComplete()) {
            HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();

            String fileName = downloadFile.getName();
            ServletContext servletContext = (ServletContext) facesContext.getExternalContext().getContext();
            String contentType = servletContext.getMimeType(fileName);
            response.setContentType(contentType);
            response.setHeader("Content-Disposition", "attachment;filename=\"" + fileName + "\"");
            InputStream in = null;
            ServletOutputStream out = null;
            try {
                in = new FileInputStream(downloadFile);
                out = response.getOutputStream();
                byte[] buffer = new byte[4096];
                int length;
                while ((length = in.read(buffer)) != -1) {
                    out.write(buffer, 0, length);
                }
                out.flush();
            } catch (IOException e) {
                logger.error("IOException while exporting run note", e);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        logger.error(e);
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        logger.error(e);
                    }
                }
            }

            facesContext.responseComplete();
        }

    }

    /**
     * move files on server folder
     */

    public void exportFiles() {
        if (selectedFiles == null || selectedFiles.isEmpty()) {
            Helper.setFehlerMeldung("noFileSelected");
            return;
        }
        List<DocStruct> allPages = metadataBean.getDocument().getPhysicalDocStruct().getAllChildren();
        List<String> filenamesToMove = new ArrayList<String>();

        for (String fileIndex : selectedFiles) {
            try {
                int index = Integer.parseInt(fileIndex);
                filenamesToMove.add(allPages.get(index).getImageName());
            } catch (NumberFormatException e) {

            }
        }
        String tempDirectory = ConfigurationHelper.getInstance().getTemporaryFolder();
        File fileuploadFolder = new File(tempDirectory + "fileupload");
        if (!fileuploadFolder.exists()) {
            fileuploadFolder.mkdir();
        }
        File destination = new File(fileuploadFolder.getAbsolutePath() + File.separator + metadataBean.getMyProzess().getTitel());
        if (!destination.exists()) {
            destination.mkdir();
        }

        for (String filename : filenamesToMove) {
            String prefix = filename.replace(Metadaten.getFileExtension(filename), "");
            String processTitle = metadataBean.getMyProzess().getTitel();
            for (String folder : metadataBean.getAllTifFolders()) {
                try {
                    File[] filesInFolder = new File(metadataBean.getMyProzess().getImagesDirectory() + folder).listFiles();
                    for (File currentFile : filesInFolder) {

                        String filenameInFolder = currentFile.getName();
                        String filenamePrefix = filenameInFolder.replace(Metadaten.getFileExtension(filenameInFolder), "");
                        if (filenamePrefix.equals(prefix)) {
                            File tempFolder = new File(destination.getAbsolutePath() + File.separator + folder);
                            if (!tempFolder.exists()) {
                                tempFolder.mkdir();
                            }

                            File destinationFile = new File(tempFolder, processTitle + "_" + currentFile.getName());

                            //                            if (deleteFilesAfterMove) {
                            //                                currentFile.renameTo(destinationFile);
                            //                            } else {
                            FileUtils.copyFile(currentFile, destinationFile);
                            //                            }
                            break;

                        }

                    }

                } catch (SwapException e) {
                    logger.error(e);
                } catch (DAOException e) {
                    logger.error(e);
                } catch (IOException e) {
                    logger.error(e);
                } catch (InterruptedException e) {
                    logger.error(e);
                }
            }
        }
        if (deleteFilesAfterMove) {
            String[] pagesArray = new String[selectedFiles.size()];
            selectedFiles.toArray(pagesArray);
            metadataBean.setAlleSeitenAuswahl(pagesArray);
            metadataBean.deleteSeltectedPages();
            selectedFiles = new ArrayList<String>();
            deleteFilesAfterMove = false;
        }

        metadataBean.retrieveAllImages();
        metadataBean.BildErmitteln(0);
    }

    public List<String> getSelectedFiles() {
        return selectedFiles;
    }

    public void setSelectedFiles(List<String> selectedFiles) {
        this.selectedFiles = selectedFiles;
    }

    public boolean isDeleteFilesAfterMove() {
        return deleteFilesAfterMove;
    }

    public void setDeleteFilesAfterMove(boolean deleteFilesAfterMove) {
        this.deleteFilesAfterMove = deleteFilesAfterMove;
    }

    public boolean isMoveFilesInAllFolder() {
        return moveFilesInAllFolder;
    }

    public void setMoveFilesInAllFolder(boolean moveFilesInAllFolder) {
        this.moveFilesInAllFolder = moveFilesInAllFolder;
    }

    /**
     * import files from folder
     * 
     */

    public List<String> getAllImportFolder() {

        String tempDirectory = ConfigurationHelper.getInstance().getTemporaryFolder();
        File fileuploadFolder = new File(tempDirectory + "fileupload");

        allImportFolder = new ArrayList<String>();
        if (fileuploadFolder.exists() && fileuploadFolder.isDirectory()) {
            allImportFolder.addAll(Arrays.asList(fileuploadFolder.list(directoryFilter)));
        }
        return allImportFolder;
    }

    public void setAllImportFolder(List<String> allImportFolder) {
        this.allImportFolder = allImportFolder;
    }

    private static FilenameFilter directoryFilter = new FilenameFilter() {
        @Override
        public boolean accept(final File dir, final String name) {
            File toTest = new File(dir, name);
            return toTest.exists() && toTest.isDirectory();
        }
    };

    public void importFiles() {

        if (selectedFiles == null || selectedFiles.isEmpty()) {
            Helper.setFehlerMeldung("noFileSelected");
            return;
        }
        String tempDirectory = ConfigurationHelper.getInstance().getTemporaryFolder();

        String masterPrefix = "";
        boolean useMasterFolder = false;
        if (ConfigurationHelper.getInstance().isUseMasterDirectory()) {
            useMasterFolder = true;
            masterPrefix = ConfigurationHelper.getInstance().getMasterDirectoryPrefix();
        }
        Process currentProcess = metadataBean.getMyProzess();
        List<String> importedFilenames = new ArrayList<String>();
        for (String importName : selectedFiles) {
            File importfolder = new File(tempDirectory + "fileupload" + File.separator + importName);
            File[] subfolderList = importfolder.listFiles();
            for (File subfolder : subfolderList) {

                if (useMasterFolder) {
                    // check if current import folder is master folder
                    if (subfolder.getName().startsWith(masterPrefix)) {
                        try {
                            String masterFolderName = currentProcess.getImagesOrigDirectory(false);
                            File masterDirectory = new File(masterFolderName);
                            if (!masterDirectory.exists()) {
                                masterDirectory.mkdir();
                            }
                            File[] objectInFolder = subfolder.listFiles();
                            List<File> sortedList = Arrays.asList(objectInFolder);
                            Collections.sort(sortedList);
                            for (File object : sortedList) {
                                FileUtils.copyFileToDirectory(object, masterDirectory);
                            }
                        } catch (SwapException e) {
                            logger.error(e);
                            Helper.setFehlerMeldung("", e);
                        } catch (DAOException e) {
                            logger.error(e);
                        } catch (IOException e) {
                            logger.error(e);
                        } catch (InterruptedException e) {
                            logger.error(e);
                        }
                    } else {
                        if (subfolder.getName().contains("_")) {
                            String folderSuffix = subfolder.getName().substring(subfolder.getName().lastIndexOf("_") + 1);
                            String folderName = currentProcess.getMethodFromName(folderSuffix);
                            if (folderName != null) {
                                try {
                                    File directory = new File(folderName);
                                    File[] objectInFolder = subfolder.listFiles();
                                    List<File> sortedList = Arrays.asList(objectInFolder);
                                    Collections.sort(sortedList);
                                    for (File object : sortedList) {
                                        if (currentProcess.getImagesTifDirectory(false).equals(folderName + File.separator)) {
                                            importedFilenames.add(object.getName());
                                        }
                                        FileUtils.copyFileToDirectory(object, directory);
                                    }

                                }

                                catch (IOException e) {
                                    logger.error(e);
                                } catch (SwapException e) {
                                    logger.error(e);
                                } catch (DAOException e) {
                                    logger.error(e);
                                } catch (InterruptedException e) {
                                    logger.error(e);
                                }

                            }
                        }
                    }

                } else {
                    if (subfolder.getName().contains("_")) {
                        String folderSuffix = subfolder.getName().substring(subfolder.getName().lastIndexOf("_") + 1);
                        String folderName = currentProcess.getMethodFromName(folderSuffix);
                        if (folderName != null) {
                            File directory = new File(folderName);
                            File[] objectInFolder = subfolder.listFiles();
                            List<File> sortedList = Arrays.asList(objectInFolder);
                            Collections.sort(sortedList);
                            for (File object : sortedList) {
                                try {
                                    if (currentProcess.getImagesTifDirectory(false).equals(folderName + File.separator)) {
                                        importedFilenames.add(object.getName());
                                    }
                                    FileUtils.copyFileToDirectory(object, directory);
                                } catch (IOException e) {
                                    logger.error(e);
                                } catch (SwapException e) {
                                    logger.error(e);
                                } catch (DAOException e) {
                                    logger.error(e);
                                } catch (InterruptedException e) {
                                    logger.error(e);
                                }
                            }
                        }
                    }
                }
            }
        }
        // update pagination
        try {
            if (insertPage == null || insertPage.isEmpty() || insertPage.equals("lastPage")) {
                metadataBean.createPagination();
            } else {
                int indexToImport = Integer.parseInt(insertPage);
                for (String filename : importedFilenames) {
                    updatePagination(filename);
                    insertPage = String.valueOf(++indexToImport);
                }
            }
        } catch (TypeNotAllowedForParentException e) {
            logger.error(e);
        } catch (SwapException e) {
            logger.error(e);
        } catch (DAOException e) {
            logger.error(e);
        } catch (MetadataTypeNotAllowedException e) {
            logger.error(e);
        } catch (IOException e) {
            logger.error(e);
        } catch (InterruptedException e) {
            logger.error(e);
        }

        // delete folder

        for (String importName : selectedFiles) {
            File importfolder = new File(tempDirectory + "fileupload" + File.separator + importName);
            try {
                FileUtils.deleteDirectory(importfolder);
            } catch (IOException e) {
                logger.error(e);
            }
        }
        metadataBean.retrieveAllImages();
        metadataBean.BildErmitteln(0);
    }

    public String getCurrentFolder() {
        return currentFolder;
    }

    public void setCurrentFolder(String currentFolder) {
        this.currentFolder = currentFolder;
    }

    private static boolean matchesFileConfiguration(String filename) {

        if (filename == null) {
            return false;
        }

        String afterLastSlash = filename.substring(filename.lastIndexOf('/') + 1);
        String afterLastBackslash = afterLastSlash.substring(afterLastSlash.lastIndexOf('\\') + 1);

        String prefix = ConfigurationHelper.getInstance().getImagePrefix();
        if (!afterLastBackslash.matches(prefix + "\\..+")) {
            return false;
        }

        return true;
    }

}