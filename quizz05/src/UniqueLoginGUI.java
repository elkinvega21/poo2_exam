import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UniqueLoginGUI extends JFrame {
    private JLabel saludoLabel;
    private JTextField usuarioField;
    private JPasswordField claveField;

    private Map<String, String> usuarios;


    //NOTA: el usuario y clave esta en donde dice usuarios.txt
    //Y la carpeta donde se guarda los registro de cada usuario y la hora esta en la carpeta ingresos, sale en la parte
    //en donde abre este programa

    public UniqueLoginGUI() {
        setTitle("Inicio de Sesión");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        saludoLabel = new JLabel("¡BIENVENIDO!");
        saludoLabel.setHorizontalAlignment(JLabel.CENTER);

        JLabel usuarioLabel = new JLabel("Usuario:");
        usuarioField = new JTextField();

        JLabel claveLabel = new JLabel("Contraseña:");
        claveField = new JPasswordField();

        JButton loginButton = new JButton("Iniciar Sesión");
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String usuario = usuarioField.getText();
                String clave = new String(claveField.getPassword());
                if (validarCredenciales(usuario, clave)) {
                    registrarIngreso(usuario);
                    JOptionPane.showMessageDialog(UniqueLoginGUI.this, "¡Inicio de sesión exitoso!");
                } else {
                    JOptionPane.showMessageDialog(UniqueLoginGUI.this, "¡Usuario o contraseña incorrectos!");
                }
            }
        });

        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.add(usuarioLabel);
        panel.add(usuarioField);
        panel.add(claveLabel);
        panel.add(claveField);
        panel.add(loginButton);

        setLayout(new BorderLayout());
        add(saludoLabel, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);

        usuarios = cargarUsuarios();
    }

    private Map<String, String> cargarUsuarios() {
        Map<String, String> usuarios = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader("usuarios.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] partes = line.split(":");
                if (partes.length == 2) {
                    usuarios.put(partes[0], partes[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return usuarios;
    }

    private boolean validarCredenciales(String usuario, String clave) {
        // Verificar si el usuario existe y la contraseña coincide
        for (Map.Entry<String, String> entry : usuarios.entrySet()) {
            if (entry.getKey().equals(usuario) && entry.getValue().equals(clave)) {
                return true;
            }
        }
        return false;
    }

    private void registrarIngreso(String usuario) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String fechaHora = dateFormat.format(new Date());
        Path ingresosFolder = Paths.get("ingresos");
        if (!Files.exists(ingresosFolder)) {
            try {
                Files.createDirectories(ingresosFolder);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
        Path logFile = ingresosFolder.resolve(usuario + "_" + fechaHora + ".log");
        try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(logFile))) {
            pw.println("Usuario: " + usuario);
            pw.println("Fecha y Hora: " + fechaHora);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Path usersFolder = Paths.get("usuarios");
        if (!Files.exists(usersFolder)) {
            try {
                Files.createDirectories(usersFolder);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        Path usuariosFile = Paths.get("usuarios.txt");
        if (!Files.exists(usuariosFile)) {
            try {
                Files.createFile(usuariosFile);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new UniqueLoginGUI().setVisible(true);
            }
        });
    }
}
