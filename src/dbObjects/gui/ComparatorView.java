package dbObjects.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import dbObjects.ApplyDiferencial;
import dbObjects.DbObject;

public class ComparatorView extends JFrame {

	private JPanel contentPane;

	/**
	 * Create the frame.
	 * @param diferencial
	 * @param serverCopy
	 * @param pathFile 
	 */
	public ComparatorView(DbObject excelCopy, DbObject serverCopy, String pathFile) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 821, 359);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setBackground(new Color(70, 130, 180));
		contentPane.setLayout(null);

		JLabel lblNewLabel = new JLabel("Se ha guardado un CSV con el nombre comparador-log.csv con el resultado de las diferencias con");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblNewLabel.setBounds(34, 13, 746, 63);
		contentPane.add(lblNewLabel);

		JLabel lblNewLabel_1 = new JLabel("¿Desea aplicar las diferencias sobre la estructura de ficheros?");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblNewLabel_1.setBounds(116, 190, 582, 36);
		contentPane.add(lblNewLabel_1);

		JLabel lblNewLabel_2 = new JLabel("el resultado de las diferencias de la comparación de metamodelos.");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblNewLabel_2.setBounds(34, 45, 756, 49);
		contentPane.add(lblNewLabel_2);

		JCheckBox chckbxNewCheckBoxInsert = new JCheckBox("Inserciones");
		chckbxNewCheckBoxInsert.setBackground(Color.LIGHT_GRAY);
		chckbxNewCheckBoxInsert.setBounds(73, 133, 135, 36);
		contentPane.add(chckbxNewCheckBoxInsert);

		JCheckBox chckbxNewCheckBoxDelete = new JCheckBox("Borrados");
		chckbxNewCheckBoxDelete.setBackground(Color.LIGHT_GRAY);
		chckbxNewCheckBoxDelete.setBounds(309, 133, 125, 36);
		contentPane.add(chckbxNewCheckBoxDelete);

		JCheckBox chckbxNewCheckBoxUpdate = new JCheckBox("Actualizaciones");
		chckbxNewCheckBoxUpdate.setBackground(Color.LIGHT_GRAY);
		chckbxNewCheckBoxUpdate.setBounds(563, 133, 135, 36);
		contentPane.add(chckbxNewCheckBoxUpdate);

		JButton btnNewButton = new JButton("Aplicar Cambios");
		btnNewButton.setBackground(Color.LIGHT_GRAY);
		btnNewButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				//Applying the changes into the Directory structure
				ApplyDiferencial result = new ApplyDiferencial();
				//Delete & insert
				if(chckbxNewCheckBoxDelete.isSelected() && chckbxNewCheckBoxInsert.isSelected()) {
					result.completeDiferencial(excelCopy, serverCopy, pathFile, true, true);
				}
				//No delete && insert
				else if (!chckbxNewCheckBoxDelete.isSelected() && chckbxNewCheckBoxInsert.isSelected()) {
					result.completeDiferencial(excelCopy, serverCopy, pathFile, false, true);
				}
				//delete && no insert
				else if (chckbxNewCheckBoxDelete.isSelected() && !chckbxNewCheckBoxInsert.isSelected()) {
					result.completeDiferencial(excelCopy, serverCopy, pathFile, true, false);
				}
				//No insert & no delete
				else {
					//no changes in the structure
				}
			}
		});
		btnNewButton.setBounds(298, 242, 155, 57);
		contentPane.add(btnNewButton);
	}
}
