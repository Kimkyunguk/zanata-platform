/*
 * Copyright 2014, Red Hat, Inc. and individual contributors as indicated by the
 * @author tags. See the copyright.txt file in the distribution for a full
 * listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.zanata.async.handle;

import org.zanata.async.AsyncTaskHandle;

/**
 * Asynchronous task handle for the copy version process.
 *
 * @author Carlos Munoz
 *         <a href="mailto:camunoz@redhat.com">camunoz@redhat.com</a>
 */
public class CopyVersionTaskHandle extends AsyncTaskHandle<Void> {

    private int documentCopied;
    private int totalDoc;
    private String cancelledBy;
    private long cancelledTime;
    private String triggeredBy;

    /**
     * Increments the processed document count by 1
     */
    public void incrementDocumentProcessed() {
        documentCopied++;
    }

    public int getDocumentCopied() {
        return this.documentCopied;
    }

    public void setDocumentCopied(final int documentCopied) {
        this.documentCopied = documentCopied;
    }

    public int getTotalDoc() {
        return this.totalDoc;
    }

    public void setTotalDoc(final int totalDoc) {
        this.totalDoc = totalDoc;
    }

    public String getCancelledBy() {
        return this.cancelledBy;
    }

    public void setCancelledBy(final String cancelledBy) {
        this.cancelledBy = cancelledBy;
    }

    public long getCancelledTime() {
        return this.cancelledTime;
    }

    public void setCancelledTime(final long cancelledTime) {
        this.cancelledTime = cancelledTime;
    }

    public String getTriggeredBy() {
        return this.triggeredBy;
    }

    public void setTriggeredBy(final String triggeredBy) {
        this.triggeredBy = triggeredBy;
    }
}
