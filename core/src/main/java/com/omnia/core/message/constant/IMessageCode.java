package com.omnia.core.message.constant;

import com.omnia.core.constant.OmniaConstants;
import com.omnia.core.message.entity.Message;
import com.omnia.core.message.model.MessageSpec;

public interface IMessageCode {
    static Message getMessage(MessageSpec messageSpec) {
        return OmniaConstants.MESSAGES.get(messageSpec.getKey());
    }
}