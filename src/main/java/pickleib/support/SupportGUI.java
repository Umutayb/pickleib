package pickleib.support;

import gpt.api.GPT;
import gpt.chat.ui.theme.SupportGUILight;

import java.util.List;

public class SupportGUI extends SupportGUILight {
    public SupportGUI(List<String> prompts, String modelName, double temperature, GPT gpt, String userName, String responderName, String chatTitle) {
        super(prompts, modelName, temperature, gpt, userName, responderName, chatTitle);
    }

    //TODO: Overwrite SupportGUILight methods to establish a DB connection, enabling custom responses from GPT instance
}
