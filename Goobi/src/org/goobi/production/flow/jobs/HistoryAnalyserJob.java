package org.goobi.production.flow.jobs;

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
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;
import org.goobi.beans.HistoryEvent;
import org.goobi.beans.Process;
import org.goobi.beans.Step;

import de.sub.goobi.helper.Helper;
import de.sub.goobi.helper.NIOFileUtils;
import de.sub.goobi.helper.enums.HistoryEventType;
import de.sub.goobi.helper.exceptions.DAOException;
import de.sub.goobi.helper.exceptions.SwapException;
import de.sub.goobi.persistence.managers.HistoryManager;
import de.sub.goobi.persistence.managers.ProcessManager;

/**
 * HistoryJob proofs History of {@link Prozess} and creates missing {@link HistoryEvent}s
 * 
 * @author Steffen Hankiewicz
 * @author Igor Toker
 * @version 15.06.2009
 */
public class HistoryAnalyserJob extends AbstractGoobiJob {
    private static final Logger logger = Logger.getLogger(HistoryAnalyserJob.class);

    /*
     * (non-Javadoc)
     * 
     * @see org.goobi.production.flow.jobs.SimpleGoobiJob#initialize()
     */
    @Override
    public String getJobName() {
        return "HistoryAnalyserJob";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.goobi.production.flow.jobs.SimpleGoobiJob#execute()
     */
    @Override
    public void execute() {
        updateHistoryForAllProcesses();
    }

    /******************************************************************************
     * update the history if necessary, which means:
     * 
     * - count storage difference in byte <br>
     * - count imagesWork difference <br>
     * - count imagesMaster difference <br>
     * - count metadata difference <br>
     * - count docstruct difference <br>
     * 
     * @param inProcess the {@link Prozess} to use
     * 
     * @return true, if any history event is updated, so the process has to to saved to database
     * @throws DAOException
     * @throws SwapException
     * @throws InterruptedException
     * @throws IOException
     *****************************************************************************/
    public static Boolean updateHistory(Process inProcess) throws IOException, InterruptedException, SwapException, DAOException {
        boolean updated = false;
        /* storage */
        if (updateHistoryEvent(inProcess, HistoryEventType.storageDifference, getCurrentStorageSize(Paths.get(inProcess
                .getProcessDataDirectory())))) {
            updated = true;
        }

        if (updateHistoryEvent(inProcess, HistoryEventType.storageWorkDifference, getCurrentStorageSize(Paths.get(inProcess.getImagesTifDirectory(
                true))))) {
            updated = true;
        }
        if (updateHistoryEvent(inProcess, HistoryEventType.storageMasterDifference, getCurrentStorageSize(Paths.get(inProcess.getImagesOrigDirectory(
                true))))) {
            updated = true;
        }

        /* imagesWork */
        Integer numberWork = NIOFileUtils.getNumberOfFiles(Paths.get(inProcess.getImagesTifDirectory(true)), ".tif");
        if (updateHistoryEvent(inProcess, HistoryEventType.imagesWorkDiff, numberWork.longValue())) {
            updated = true;
        }

        /* imagesMaster */
        Integer numberMaster = NIOFileUtils.getNumberOfFiles(Paths.get(inProcess.getImagesOrigDirectory(true)), ".tif");
        if (updateHistoryEvent(inProcess, HistoryEventType.imagesMasterDiff, numberMaster.longValue())) {
            updated = true;
        }

        /* metadata */
        if (updateHistoryEvent(inProcess, HistoryEventType.metadataDiff, inProcess.getSortHelperMetadata().longValue())) {
            updated = true;
        }

        /* docstruct */
        if (updateHistoryEvent(inProcess, HistoryEventType.docstructDiff, inProcess.getSortHelperDocstructs().longValue())) {
            updated = true;
        }

        return updated;
    }

    /****************************************************************************
     * update history for each {@link Step} of given {@link Prozess}
     * 
     * @param inProcess given {@link Prozess}
     * @return true, if changes are made and have to be saved to database
     ***************************************************************************/
    @SuppressWarnings("incomplete-switch")
    private static Boolean updateHistoryForSteps(Process inProcess) {
        Boolean isDirty = false;
        HistoryEvent he = null;

        /**
         * These are the patterns, which must be set, if a pattern differs from these something is wrong, timestamp pattern overrules status, in that
         * case status gets changed to match one of these pattern
         * 
         * <pre>
         *         status |  begin    in work    work done
         *         -------+-------------------------------  
         *           0    |  null     null       null
         *           1    |  null     null       null
         *           2    |  set      set        null
         *           3    |  set      set        set
         * </pre>
         */

        for (Step step : inProcess.getSchritteList()) {

            switch (step.getBearbeitungsstatusEnum()) {

                case DONE:
                    // fix missing start date
                    if (step.getBearbeitungsbeginn() == null) {
                        isDirty = true;
                        if (step.getBearbeitungszeitpunkt() == null) {
                            step.setBearbeitungsbeginn(getTimestampFromPreviousStep(inProcess, step));
                        } else {
                            step.setBearbeitungsbeginn(step.getBearbeitungszeitpunkt());
                        }
                    }

                    // fix missing editing date
                    if (step.getBearbeitungszeitpunkt() == null) {
                        isDirty = true;
                        if (step.getBearbeitungsende() == null) {
                            step.setBearbeitungszeitpunkt(step.getBearbeitungsbeginn());
                        } else {
                            step.setBearbeitungszeitpunkt(step.getBearbeitungsende());
                        }
                    }

                    // fix missing end date
                    if (step.getBearbeitungsende() == null) {
                        isDirty = true;
                        step.setBearbeitungsende(step.getBearbeitungszeitpunkt());
                    }

                    // attempts to add a history event,
                    // exists method returns null if event already exists
                    he = addHistoryEvent(step.getBearbeitungsende(), step.getReihenfolge(), step.getTitel(), HistoryEventType.stepDone, inProcess);

                    if (he != null) {
                        isDirty = true;
                    }

                    // for each step done we need to create a step open event on that step based on the latest timestamp for the previous step
                    he = addHistoryEvent(getTimestampFromPreviousStep(inProcess, step), step.getReihenfolge(), step.getTitel(),
                            HistoryEventType.stepOpen, inProcess);

                    if (he != null) {
                        isDirty = true;
                    }

                    break;

                case INWORK:

                    // fix missing start date
                    if (step.getBearbeitungsbeginn() == null) {
                        isDirty = true;
                        if (step.getBearbeitungszeitpunkt() == null) {
                            step.setBearbeitungsbeginn(getTimestampFromPreviousStep(inProcess, step));
                        } else {
                            step.setBearbeitungsbeginn(step.getBearbeitungszeitpunkt());
                        }
                    }

                    // fix missing editing date
                    if (step.getBearbeitungszeitpunkt() == null) {
                        isDirty = true;
                        step.setBearbeitungszeitpunkt(step.getBearbeitungsbeginn());
                    }

                    // enc date must be null
                    if (step.getBearbeitungsende() != null) {
                        step.setBearbeitungsende(null);
                        isDirty = true;
                    }

                    he = addHistoryEvent(step.getBearbeitungsbeginn(), step.getReihenfolge(), step.getTitel(), HistoryEventType.stepInWork,
                            inProcess);

                    if (he != null) {
                        isDirty = true;
                    }

                    //
                    // for each step inwork we need to create a step open event on that step based on the latest timestamp from the previous step
                    he = addHistoryEvent(getTimestampFromPreviousStep(inProcess, step), step.getReihenfolge(), step.getTitel(),
                            HistoryEventType.stepOpen, inProcess);

                    if (he != null) {
                        isDirty = true;
                    }

                    break;

                case OPEN:

                    // fix set start date - decision is that reopened (and therfore with timestamp for begin) shouldn't be reset
                    /*
                     * if (step.getBearbeitungsbeginn() != null) { step.setBearbeitungsbeginn(null); isDirty = true; }
                     */

                    // fix missing editing date
                    if (step.getBearbeitungszeitpunkt() == null) {
                        isDirty = true;
                        if (step.getBearbeitungsende() != null) {
                            step.setBearbeitungszeitpunkt(step.getBearbeitungsende());
                        } else {
                            // step.setBearbeitungsbeginn(getTimestampFromPreviousStep(inProcess, step));
                            step.setBearbeitungszeitpunkt(getTimestampFromPreviousStep(inProcess, step));
                        }
                    }

                    // fix set end date
                    if (step.getBearbeitungsende() != null) {
                        step.setBearbeitungsende(null);
                        isDirty = true;
                    }

                    he = addHistoryEvent(step.getBearbeitungszeitpunkt(), step.getReihenfolge(), step.getTitel(), HistoryEventType.stepOpen,
                            inProcess);

                    if (he != null) {
                        isDirty = true;
                    }

                    break;
            }

            // check corrections timestamp this clearly only works on past
            // correction events done in the german language current corrections
            // directly adds to the history

            // adds for each step a step locked on the basis of the process creation timestamp (new in 1.6)
            he = addHistoryEvent(inProcess.getErstellungsdatum(), step.getReihenfolge(), step.getTitel(), HistoryEventType.stepLocked, inProcess);

            if (he != null) {
                isDirty = true;
            }

            //			if (step.getEigenschaftenSize() > 0) {
            //
            //				for (Schritteigenschaft prop : step.getEigenschaftenList()) {
            //					if (prop.getType().equals(PropertyType.messageError)) {
            //						Date myDate = prop.getCreationDate();
            //						if (myDate == null && step.getBearbeitungszeitpunkt() != null) {
            //							myDate = step.getBearbeitungszeitpunkt();
            //						}
            //						if (myDate != null) {
            //							he = addHistoryEvent(myDate, step.getReihenfolge(), step.getTitel(), HistoryEventType.stepError, inProcess);
            //							if (he != null) {
            //								isDirty = true;
            //							}
            //						}
            //					}
            //				}
            //			}
        }

        // this method removes duplicate items from the history list, which
        // already happened to be there, isDirty will be automatically be set
        if (getHistoryEventDuplicated(inProcess)) {
            isDirty = true;
        }

        return isDirty;
    }

    /**
     * 
     * @param timeStamp
     * @param stepOrder
     * @param stepName
     * @param type
     * @param inProcess
     * @return History event if event needs to be added, null if event(same kind, same time, same process ) already exists
     */
    private static HistoryEvent addHistoryEvent(Date timeStamp, Integer stepOrder, String stepName, HistoryEventType type, Process inProcess) {
        HistoryEvent he = new HistoryEvent(timeStamp, stepOrder, stepName, type, inProcess);

        if (!getHistoryContainsEventAlready(he, inProcess)) {
            HistoryManager.addHistoryEvent(he);
            return he;
        } else {
            return null;
        }
    }

    /****************************************************************************
     * check if history already contains given event
     * 
     * @param inEvent given {@link HistoryEvent}
     * @param inProcess given {@link Prozess}
     * @return true, if {@link HistoryEvent} already exists
     ***************************************************************************/
    private static Boolean getHistoryContainsEventAlready(HistoryEvent inEvent, Process inProcess) {
        List<HistoryEvent> list = HistoryManager.getHistoryEvents(inProcess.getId());
        for (HistoryEvent historyItem : list) {
            if (inEvent.getId() == null || !inEvent.getId().equals(historyItem.getId())) { // this is required, in case items
                // from the same list are compared
                if (historyItem.equals(inEvent)) {
                    return true;
                }
            }
        }
        return false;
    }

    /****************************************************************************
     * get stored value (all diffs as sum) from history
     * 
     * @return stored value as Long
     ***************************************************************************/
    private static Long getStoredValue(Process inProcess, HistoryEventType inType) {
        long storedValue = 0;
        List<HistoryEvent> list = HistoryManager.getHistoryEvents(inProcess.getId());
        for (HistoryEvent historyItem : list) {
            if (historyItem.getHistoryType() == inType) {
                storedValue += historyItem.getNumericValue().longValue();
            }
        }
        return storedValue;
    }

    /****************************************************************************
     * update history, if current value is different to stored value
     * 
     * @return true if value is different and history got updated, else false
     ***************************************************************************/
    private static Boolean updateHistoryEvent(Process inProcess, HistoryEventType inType, Long inCurrentValue) {
        long storedValue = getStoredValue(inProcess, inType);
        long diff = inCurrentValue - storedValue;

        // if storedValue is different to current value - update history
        if (diff != 0) {
            HistoryManager.addHistory(new Date(), diff, null, inType.getValue(), inProcess.getId());
            return true;
        } else {
            return false;
        }
    }

    /****************************************************************************
     * Size of Storage in Bytes per {@link Prozess}
     * 
     * @return size in bytes, or 0 if error.
     * @throws DAOException
     * @throws SwapException
     * @throws InterruptedException
     * @throws IOException
     ***************************************************************************/
    private static long getCurrentStorageSize(Path directory) throws IOException {
        if (!Files.exists(directory)) {
            return 0;
        }
        if (Files.isDirectory(directory)) {
            return size(directory);
        } else {
            return Files.size(directory);
        }
    }

    /**
     * Calculate the size of a directory by using NIOs walkFileTree it ignores symlinks, folders without permissions and concurrent modification
     * 
     */

    private static long size(Path path) {

        final AtomicLong size = new AtomicLong(0);

        try {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {

                    size.addAndGet(attrs.size());
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException e) {

                    logger.debug("skipped: " + file, e);
                    // Skip folders that can't be traversed
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException e) {

                    if (e != null)
                        logger.debug("had trouble traversing: " + dir, e);
                    // Ignore errors traversing a folder
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new AssertionError("walkFileTree will not throw IOException if the FileVisitor does not");
        }

        return size.get();
    }

    /***************************************************************************
     * updateHistoryForAllProcesses
     **************************************************************************/
    public void updateHistoryForAllProcesses() {
        logger.info("start history updating for all processes");
        try {
            //			Session session = Helper.getHibernateSession();
            //			Query query = session.createQuery("from Prozess order by id desc");
            List<Process> processList = ProcessManager.getAllProcesses();

            //			Iterator<Process> it = query.iterate();
            //			int i = 0;
            //			while (it.hasNext()) {
            //				i++;
            //				Process proc = it.next();
            for (Process proc : processList) {
                if (logger.isDebugEnabled()) {
                    logger.debug("updating history entries for " + proc.getTitel());
                }
                try {
                    if (!proc.isSwappedOutGui()) {
                        if (true == updateHistory(proc) | updateHistoryForSteps(proc)) {
                            ProcessManager.saveProcess(proc);
                            if (logger.isDebugEnabled()) {
                                logger.debug("history updated for process " + proc.getId());
                            }
                        }
                    }

                    //					// commit transaction every 50 items
                    //					if (!it.hasNext() || i % 50 == 0) {
                    //						session.flush();
                    //						session.beginTransaction().commit();
                    //						session.clear();
                    //					}
                    //
                    //				} catch (HibernateException e) {
                    //					logger.error("HibernateException occured while scheduled storage calculation", e);

                } catch (Exception e) {
                    Helper.setFehlerMeldung("An error occured while scheduled storage calculation", e);
                    logger.error("ServletException occured while scheduled storage calculation", e);
                }
            }
        } catch (Exception e) {
            Helper.setFehlerMeldung("Another Exception occured while scheduled storage calculation", e);
            logger.error("Another Exception occured while scheduled storage calculation", e);
        }
        logger.info("end history updating for all processes");
    }

    /**
     * method returns a timestamp from a previous step, iterates through the steps if necessary
     * 
     * @param stepOrder
     */
    private static Date getTimestampFromPreviousStep(Process inProcess, Step inStep) {
        Date eventTimestamp = null;
        List<Step> tempList = inProcess.getSchritteList();

        for (Step s : tempList) {
            // making sure that we only look for timestamps in the step below
            // this one
            int index = tempList.indexOf(s);

            if (s == inStep && index != 0) {
                Step prevStep = tempList.get(index - 1);

                if (prevStep.getBearbeitungsende() != null) {
                    return prevStep.getBearbeitungsende();
                }

                if (prevStep.getBearbeitungszeitpunkt() != null) {
                    return prevStep.getBearbeitungszeitpunkt();
                }

                if (prevStep.getBearbeitungsbeginn() != null) {
                    return prevStep.getBearbeitungsbeginn();
                }

                eventTimestamp = getTimestampFromPreviousStep(inProcess, prevStep);
            }

        }

        if (eventTimestamp == null) {
            if (inProcess.getErstellungsdatum() != null) {
                eventTimestamp = inProcess.getErstellungsdatum();
            } else { // if everything fails we use the current date
                Calendar cal = Calendar.getInstance();
                cal.set(2007, 0, 1, 0, 0, 0);
                eventTimestamp = cal.getTime();
                logger.info("We had to use 2007-1-1 date '" + eventTimestamp.toString() + "' for a history event as a fallback");
            }

        }
        return eventTimestamp;
    }

    /**
     * method iterates through the event list and checks if there are duplicate entries, if so it will remove the entry and return a true
     * 
     * @param inProcess
     * @return
     */
    private static Boolean getHistoryEventDuplicated(Process inProcess) {
        Boolean duplicateEventRemoved = false;
        List<HistoryEvent> list = HistoryManager.getHistoryEvents(inProcess.getId());
        for (HistoryEvent he : list) {
            if (getHistoryContainsEventAlready(he, inProcess)) {
                HistoryManager.deleteHistoryEvent(he);

                duplicateEventRemoved = true;
            }
        }
        return duplicateEventRemoved;
    }

    public static Boolean updateHistoryForProzess(Process inProc) {
        Boolean updated = true;
        try {
            updateHistory(inProc);
            updateHistoryForSteps(inProc);
        } catch (Exception ex) {
            logger.warn("Updating history failed.", ex);
            updated = false;
        }
        return updated;

    }

}
