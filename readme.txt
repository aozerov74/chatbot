ssh 35.236.5.73
java --add-modules java.xml.bind -jar chatbot.jar &
java --add-modules java.xml.bind -classpath /opt/download/cb_0.17.jar:/opt/download/chatbot.jar com.chatbot.ChatbotApiHost
