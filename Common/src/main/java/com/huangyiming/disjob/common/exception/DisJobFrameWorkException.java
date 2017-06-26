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
 * wrapper client exception.
 * 
 * @author maijunsheng
 * 
 */
public class DisJobFrameWorkException extends DisJobAbstractUncheckException {
    private static final long serialVersionUID = -1638857395789735293L;

    public DisJobFrameWorkException() {
        super(""+DisJobErrorMsgConstant.JOB_DEFAULT_ERROR_CODE);
    }

    public DisJobFrameWorkException(DisJobErrorMsg motanErrorMsg) {
        super(motanErrorMsg);
    }

    public DisJobFrameWorkException(String message) {
        super(message, DisJobErrorMsgConstant.SERVICE_DEFAULT_ERROR);
    }

    public DisJobFrameWorkException(String message, DisJobErrorMsg motanErrorMsg) {
        super(message, motanErrorMsg);
    }

    public DisJobFrameWorkException(String message, Throwable cause) {
        super(message, cause, DisJobErrorMsgConstant.SERVICE_DEFAULT_ERROR);
    }

    public DisJobFrameWorkException(String message, Throwable cause, DisJobErrorMsg motanErrorMsg) {
        super(message, cause, motanErrorMsg);
    }

    public DisJobFrameWorkException(Throwable cause) {
        super(cause, DisJobErrorMsgConstant.SERVICE_DEFAULT_ERROR);
    }

    public DisJobFrameWorkException(Throwable cause, DisJobErrorMsg motanErrorMsg) {
        super(cause, motanErrorMsg);
    }

}
