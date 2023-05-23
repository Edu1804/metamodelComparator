package dbObjects.gui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

import dbObjects.CompararModelos;
import dbObjects.DbObject;
import dbObjects.DbServer;
import extractorLoader.directory.LoadFiles;
import extractorLoader.excel.LoadExcel;

public class MenuForm extends JFrame {
	static Container content=null;
	String choosertitle;
	JFileChooser chooser;
	private JPanel contentPane;
	private JTextField textExcel;
	private JTextField textFile;
	private JButton btnDiferencial;
	private JLabel validation;
	private JLabel lblNewLabel_1;
	private JLabel lblRutaDelDirectorio;
	private JTextField textServer;
	private JTextField textBBDD;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MenuForm frame = new MenuForm();
					content = frame.getContentPane();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MenuForm() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(400, 200, 968, 685);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(70, 130, 180));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		textExcel = new JTextField();
		textExcel.setFont(new Font("Arial", Font.PLAIN, 17));
		textExcel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		textExcel.setBounds(264, 111, 586, 59);
		contentPane.add(textExcel);
		textExcel.setColumns(10);
		textFile = new JTextField();
		textFile.setFont(new Font("Arial", Font.PLAIN, 17));
		textFile.setBounds(264, 204, 586, 53);
		contentPane.add(textFile);
		textFile.setColumns(10);
		JButton btnCsv = new JButton("Resumen Excel");

		btnCsv.setFont(new Font("Tahoma", Font.BOLD, 14));
		btnCsv.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnCsv.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(!textExcel.getText().isEmpty() && !textServer.getText().isEmpty() && !textBBDD.getText().isEmpty()) {
					validation.setText("");
					chooser = new JFileChooser(); 
					chooser.setCurrentDirectory(new java.io.File("."));
					chooser.setDialogTitle(choosertitle);
					chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					chooser.setAcceptAllFileFilterUsed(false);
					validation.setForeground(Color.BLACK);
					validation.setText("Cargando los datos...");
					if (chooser.showOpenDialog(content) == JFileChooser.APPROVE_OPTION) { 
						File selFile = chooser.getSelectedFile();
						LoadExcel excel1 = new LoadExcel();
						DbServer dbServer = excel1.readExcel(textExcel.getText(), textServer.getText(), textBBDD.getText());
						validation.setForeground(Color.BLACK);
						validation.setText("El archivo se ha guardado en la ruta especificada.");
						if(dbServer==null) {
							validation.setForeground(new Color(250, 128, 114));
							validation.setText("No se ha encontrado el excel. Por favor, revise la ruta.");
						} else {
							DbObject csv = new DbObject();
							csv.saveDissocToFile(selFile, dbServer);
						}
					}
					else {
						validation.setForeground(new Color(250, 128, 114));
						validation.setText("Seleccione el directorio");
					}
				}else {
					validation.setForeground(new Color(250, 128, 114));
					validation.setText("Introduce la ruta del excel, el nombre del servidor y de la base de datos. ");
				}
			}
		});
		btnCsv.setBounds(264, 468, 135, 41);
		contentPane.add(btnCsv);
		JLabel lblNewLabel = new JLabel("Comparador");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 24));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(439, 27, 183, 58);
		contentPane.add(lblNewLabel);
		btnDiferencial = new JButton("Diferencial");
		btnDiferencial.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnDiferencial.setFont(new Font("Tahoma", Font.BOLD, 14));
		btnDiferencial.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(!textExcel.getText().isEmpty() && !textFile.getText().isEmpty() && !textServer.getText().isEmpty() && !textBBDD.getText().isEmpty()) {
					validation.setText("Cargando los datos...");
					validation.setForeground(Color.BLACK);
					System.out.println("Inicilizando comparador de Metamodelos\n");
					//pasamos de estructura excel a estructura de objetos
					LoadExcel excel = new LoadExcel();
					DbServer serverExcel = excel.readExcel(textExcel.getText(), textServer.getText(), textBBDD.getText());

					if(serverExcel==null) {
						validation.setForeground(new Color(250, 128, 114));
						validation.setText("Ruta del excel no encontrada");
					}else {
						validation.setText("");
						//pasamos de estructura de ficheros a estructura de objetos
						LoadFiles files = new LoadFiles();
						DbServer serverFile= files.initialFiles(textFile.getText().trim());
						if(serverFile==null) {
							validation.setForeground(new Color(250, 128, 114));
							validation.setText("Ruta del sistema de ficheros no encontrada");
						}
						else {
							validation.setText("");

							validation.setText("");
							chooser = new JFileChooser(); 
							chooser.setCurrentDirectory(new java.io.File("."));
							chooser.setDialogTitle(choosertitle);
							chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
							chooser.setAcceptAllFileFilterUsed(false);
							if (chooser.showOpenDialog(content) == JFileChooser.APPROVE_OPTION) { 
								validation.setText("");
								File selFile = chooser.getSelectedFile();
								//obtenemos el diferencial de ambos modelos
								CompararModelos comparador = new CompararModelos();
								comparador.comparacionRoot(serverExcel, serverFile, selFile, textServer.getText().trim(), textFile.getText().trim());
								ComparatorView comparatorView = new ComparatorView(serverExcel, serverFile, textFile.getText().trim());
								comparatorView.setVisible(true);
							}
							else {
								validation.setForeground(new Color(250, 128, 114));
								validation.setText("Seleccione el directorio");
							}
						}
					}
				}
				else {
					validation.setForeground(new Color(250, 128, 114));
					validation.setText("Introduce ambas rutas y los nombres del servidor y base de datos.");
				}
			}
		});
		btnDiferencial.setBounds(730, 468, 120, 41);
		contentPane.add(btnDiferencial);

		validation = new JLabel("");
		validation.setFont(new Font("Tahoma", Font.PLAIN, 18));
		validation.setForeground(new Color(250, 128, 114));
		validation.setBounds(264, 436, 586, 29);
		contentPane.add(validation);

		lblNewLabel_1 = new JLabel("Ruta del excel");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblNewLabel_1.setMinimumSize(new Dimension(105, 16));
		lblNewLabel_1.setMaximumSize(new Dimension(105, 16));
		lblNewLabel_1.setPreferredSize(new Dimension(105, 16));
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.LEFT);
		lblNewLabel_1.setHorizontalTextPosition(SwingConstants.CENTER);
		lblNewLabel_1.setBounds(83, 127, 156, 25);
		contentPane.add(lblNewLabel_1);

		lblRutaDelDirectorio = new JLabel("Ruta del directorio");
		lblRutaDelDirectorio.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblRutaDelDirectorio.setHorizontalTextPosition(SwingConstants.CENTER);
		lblRutaDelDirectorio.setHorizontalAlignment(SwingConstants.LEFT);
		lblRutaDelDirectorio.setBounds(83, 217, 168, 25);
		contentPane.add(lblRutaDelDirectorio);

		textServer = new JTextField();
		textServer.setFont(new Font("Arial", Font.PLAIN, 17));
		textServer.setColumns(10);
		textServer.setBounds(264, 286, 586, 53);
		contentPane.add(textServer);

		textBBDD = new JTextField();
		textBBDD.setFont(new Font("Arial", Font.PLAIN, 17));
		textBBDD.setColumns(10);
		textBBDD.setBounds(264, 370, 586, 53);
		contentPane.add(textBBDD);

		JLabel lblNombreServidor = new JLabel("Nombre servidor");
		lblNombreServidor.setHorizontalTextPosition(SwingConstants.CENTER);
		lblNombreServidor.setHorizontalAlignment(SwingConstants.LEFT);
		lblNombreServidor.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblNombreServidor.setBounds(83, 304, 168, 25);
		contentPane.add(lblNombreServidor);

		JLabel lblNombreBbdd = new JLabel("Nombre BBDD");
		lblNombreBbdd.setHorizontalTextPosition(SwingConstants.CENTER);
		lblNombreBbdd.setHorizontalAlignment(SwingConstants.LEFT);
		lblNombreBbdd.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblNombreBbdd.setBounds(83, 370, 168, 25);
		contentPane.add(lblNombreBbdd);

//		JButton btnHelp = new JButton("help");
//		btnHelp.addMouseListener(new MouseAdapter() {
//			@Override
//			public void mouseClicked(MouseEvent e) {
//				try {
//					File file = new java.io.File("help/index.html").getAbsoluteFile();
//					Desktop.getDesktop().open(file);                    
//				} catch (Exception e1) {
//					e1.printStackTrace();
//				}
//			}
//		});
//
//		btnHelp.setFont(new Font("Tahoma", Font.BOLD, 14));
//		btnHelp.setBounds(780, 40, 70, 41);
//		contentPane.add(btnHelp);
	}
}
