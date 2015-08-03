/*
 * Copyright (C) 2009-2015  Pivotal Software, Inc
 *
 * This program is is free software; you can redistribute it and/or modify
 * it under the terms version 2 of the GNU General Public License as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package com.springsource.hq.plugin.tcserver.cli.client.configuration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.httpclient.HttpMethodBase;
import org.hyperic.hq.hqapi1.ResponseHandler;

import com.springsource.hq.plugin.tcserver.cli.client.schema.Response;
import com.springsource.hq.plugin.tcserver.cli.client.schema.ResponseStatus;
import com.springsource.hq.plugin.tcserver.cli.client.schema.ServiceError;

public final class FileResponseHandler<T extends Response> implements ResponseHandler<T> {

    private final File targetFile;

    private final Class<T> clazz;

    /**
     * 
     * @param targetFile The local file to which the contents of the method response body should be written
     */
    public FileResponseHandler(File targetFile, Class<T> clazz) {
        this.targetFile = targetFile;
        this.clazz = clazz;
    }

    public T getErrorResponse(org.hyperic.hq.hqapi1.types.ServiceError error) throws IOException {
        try {
            T ret = clazz.newInstance();

            ret.setStatus(ResponseStatus.FAILURE);

            ServiceError serviceError = new ServiceError();
            serviceError.setErrorCode(error.getErrorCode());
            serviceError.setReasonText(error.getReasonText());
            ret.setError(serviceError);

            return ret;
        } catch (Exception e) {
            throw new IOException("Error occurred when generating error response: " + e.getMessage(), e);
        }
    }

    private T getSuccessResponse() throws IOException {
        try {
            T ret = clazz.newInstance();
            ret.setStatus(ResponseStatus.SUCCESS);
            return ret;
        } catch (Exception e) {
            throw new IOException("Error occurred when generating success response: " + e.getMessage(), e);
        }
    }

    public T handleResponse(int responseCode, HttpMethodBase method) throws IOException {
        switch (responseCode) {
            case 200:
                FileOutputStream fileOutputStream = null;
                InputStream in = method.getResponseBodyAsStream();
                try {
                    fileOutputStream = new FileOutputStream(targetFile.getAbsolutePath());
                    final byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        fileOutputStream.write(buf, 0, len);
                    }
                    return getSuccessResponse();
                } catch (Exception e) {
                    return getErrorResponse(createServiceError("UnexpectedError", "Unable to deserialize result"));
                } finally {
                    if (fileOutputStream != null) {
                        try {
                            fileOutputStream.close();
                        } catch (IOException e) {
                        }
                    }
                }
            case 401:
                // Unauthorized
                return getErrorResponse(createServiceError("LoginFailure", "The given username and password could not be validated"));
            default:
                String reasonText;
                if (method.getStatusText() != null) {
                    reasonText = method.getStatusText();
                } else {
                    reasonText = "An unexpected error occurred";
                }
                return getErrorResponse(createServiceError("UnexpectedError", reasonText));
        }
    }

    private org.hyperic.hq.hqapi1.types.ServiceError createServiceError(String errorCode, String reasonText) {
        org.hyperic.hq.hqapi1.types.ServiceError error = new org.hyperic.hq.hqapi1.types.ServiceError();
        error.setErrorCode(errorCode);
        error.setReasonText(reasonText);
        return error;
    }
}
