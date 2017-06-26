/*
 *  Copyright 2009-2016 Weibo, Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.huangyiming.disjob.common.exception;
 

/**
 * @author Disjob
 * @version 创建时间：2016-5-19
 * 
 */
public abstract class DisJobAbstractCheckException extends Exception {
    private static final long serialVersionUID = -7742311167276890505L;

    protected DisJobErrorMsg disJobErrorMsg;
   
    protected String errorMsg = null;

    public DisJobAbstractCheckException() {
        super();
    }

    public DisJobAbstractCheckException(DisJobErrorMsg disJobErrorMsg) {
        super();
        this.disJobErrorMsg = disJobErrorMsg;
    }

    public DisJobAbstractCheckException(String message) {
        super(message);
        this.errorMsg = message;
    }

    public DisJobAbstractCheckException(String message, DisJobErrorMsg disJobErrorMsg) {
        super(message);
        this.disJobErrorMsg = disJobErrorMsg;
        this.errorMsg = message;
    }

    public DisJobAbstractCheckException(String message, Throwable cause) {
        super(message, cause);
        this.errorMsg = message;
    }

    public DisJobAbstractCheckException(String message, Throwable cause, DisJobErrorMsg disJobErrorMsg) {
        super(message, cause);
        this.disJobErrorMsg = disJobErrorMsg;
        this.errorMsg = message;
    }

    public DisJobAbstractCheckException(Throwable cause) {
        super(cause);
    }

    public DisJobAbstractCheckException(Throwable cause, DisJobErrorMsg disJobErrorMsg) {
        super(cause);
        this.disJobErrorMsg = disJobErrorMsg;
    }

    @Override
    public String getMessage() {
        if (disJobErrorMsg == null) {
            return super.getMessage();
        }
        String message;
        if (errorMsg != null && !"".equals(errorMsg)) {
            message = errorMsg;
        } else {
            message = disJobErrorMsg.getMessage();
        }
        // TODO 统一上下文 requestid
        return "error_message: " + message + ", status: " + disJobErrorMsg.getStatus() + ", error_code: " + disJobErrorMsg.getErrorCode()
                + ",r=";
    }

    public int getStatus() {
        return disJobErrorMsg != null ? disJobErrorMsg.getStatus() : 0;
    }

    public int getErrorCode() {
        return disJobErrorMsg != null ? disJobErrorMsg.getErrorCode() : 0;
    }

    public DisJobErrorMsg getDisJobErrorMsg() {
        return disJobErrorMsg;
    }
}
