package digitalOcean.ssh;

import com.jcraft.jsch.*;
import digitalOcean.config.APIKey;

import java.io.InputStream;

public class Command {

    static Channel channel;
    static Session session;
    static JSch jsch;
    boolean connected = false;

    private static Command command;

    public static Command getInstance() {
        if (command == null)
            command = new Command();
        return command;
    }

    @SuppressWarnings("SameParameterValue")
    public String runCommand(String command, String user, String host, String password) {
        StringBuilder res = new StringBuilder();
        try {
            System.out.println("InputStream");
            if (jsch == null)
                connect(user, host, password);
            if (!connected)
                return null;
            channel = session.openChannel("exec");
            channel.setInputStream(System.in);
            channel.setOutputStream(System.out);
            InputStream in = channel.getInputStream();

            ((ChannelExec) channel).setCommand(command);
            channel.connect();

            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int read = in.read(tmp, 0, 1024);
                    if (read < 0) break;
                    res.append(new String(tmp, 0, read));
                }
                if (channel.isClosed()) break;
            }
        } catch (JSchException sshexeption) {
            System.out.println("SESSION IS DOWN");
        } catch (Exception ig) {
            ig.printStackTrace();
            disconnect();
        }
        return res.toString();
    }


    public void connect(String user, String host, String password) {
        try {
            jsch = new JSch();
            session = jsch.getSession(user, host, 22);
            session.setTimeout(3000);
            session.setUserInfo(new MyUserInfo());
            if (APIKey.usingPassword) {
                session.setPassword(password);
            } else {
                jsch.addIdentity(APIKey.keyPath);
            }
            session.connect();
            connected = true;
        } catch (JSchException e) {
            jsch = null;
            System.out.println("Auth Cancel");
            connected = false;
        }
    }


    public void disconnect() {
        if (channel != null)
            channel.disconnect();
        if (session != null)
            session.disconnect();
        jsch = null;
        channel = null;
        session = null;
        connected = false;
    }

    public void reconnect(String login, String ip, String pwdFieldText) {
        disconnect();
        connect(login, ip, pwdFieldText);
    }


    public static class MyUserInfo implements UserInfo {
        public String getPassword() {
            return null;
        }

        public boolean promptYesNo(String str) {
            return true;
        }

        public String getPassphrase() {
            return null;
        }

        public boolean promptPassphrase(String message) {
            return true;
        }

        public boolean promptPassword(String message) {
            return true;
        }

        public void showMessage(String message) {
        }
    }
}