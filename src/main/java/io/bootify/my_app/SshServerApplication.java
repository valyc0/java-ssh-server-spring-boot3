package io.bootify.my_app;

import org.apache.sshd.common.session.Session;
import org.apache.sshd.common.util.net.SshdSocketAddress;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.forward.ForwardingFilter;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.shell.InteractiveProcessShellFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Paths;

@SpringBootApplication
public class SshServerApplication implements CommandLineRunner {

    @Value("${ssh.port:2223}")
    private int sshPort;

    public static void main(String[] args) {
        SpringApplication.run(SshServerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        startSshServer();
    }

    private void startSshServer() throws IOException {
        SshServer sshd = SshServer.setUpDefaultServer();
        sshd.setPort(sshPort);
        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(Paths.get("hostkey.ser")));

        // Autenticazione con username e password
        sshd.setPasswordAuthenticator(new PasswordAuthenticator() {
            @Override
            public boolean authenticate(String username, String password, ServerSession session) {
                return "user".equals(username) && "password".equals(password);
            }
        });

        // Configurazione di una shell di base per mantenere la connessione aperta
        sshd.setShellFactory(new InteractiveProcessShellFactory());

        // Abilita il port forwarding
        sshd.setForwardingFilter(new ForwardingFilter() {

			@Override
			public boolean canForwardAgent(Session session, String requestType) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean canForwardX11(Session session, String requestType) {
				// TODO Auto-generated method stub
				return true;
			}

			@Override
			public boolean canListen(SshdSocketAddress address, Session session) {
				// TODO Auto-generated method stub
				return true;
			}

			@Override
			public boolean canConnect(Type type, SshdSocketAddress address, Session session) {
				// TODO Auto-generated method stub
				return true;
			}
         
        });

        sshd.start();
        System.out.println("SSH Server started on port " + sshPort);
    }
}
