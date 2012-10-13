package org.jzy3d.bridge.swing;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jzy3d.bridge.IFrame;
import org.jzy3d.chart.Chart;



public class FrameSwing extends JFrame implements IFrame{
	public FrameSwing(Chart chart, Rectangle bounds, String title){
		this.chart = chart;

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container contentPane = getContentPane();
		BorderLayout layout = new BorderLayout();
		contentPane.setLayout(layout);
		
		addWindowListener(new WindowAdapter() { 
			public void windowClosing(WindowEvent e) {
				FrameSwing.this.remove((java.awt.Component)FrameSwing.this.chart.getCanvas());
				FrameSwing.this.chart.dispose();
				FrameSwing.this.chart = null;
				FrameSwing.this.dispose();
			}
		});
		
		JPanel panel3d = new JPanel();
		panel3d.setLayout(new java.awt.BorderLayout());
		panel3d.add((JComponent)chart.getCanvas());
		
		contentPane.add((JComponent)chart.getCanvas(), BorderLayout.CENTER);
		setVisible(true);
		setTitle(title + "[Swing]");
		pack();
		setBounds(bounds);
	}
	
	private Chart chart;
	private static final long serialVersionUID = 6474157681794629264L;
}