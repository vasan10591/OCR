package ocr.neural.net.swingPaint;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import ocr.neural.net.OCRNeuralNet;

public class SwingPaint {
    
    int count = 0;
    static OCRNeuralNet net;
    JButton clearBtn,queryBtn,train,save,viewReverse;
    JTextField field;
    DrawArea drawArea;
    ActionListener actionListener = new ActionListener(){
        @Override
        public void actionPerformed(ActionEvent e){
            if(e.getSource()==clearBtn){
                drawArea.clear();
                field.setText("");
            } else if(e.getSource() == queryBtn){
                Image image = drawArea.getImage().getScaledInstance(28, 28, Image.SCALE_SMOOTH);
                int[] inputList = new int[784];
                PixelGrabber grabber = new PixelGrabber(image, 0, 0, 28, 28, inputList, 0, 28);
                try {
                    grabber.grabPixels();
                } catch (InterruptedException ex) {
                    Logger.getLogger(SwingPaint.class.getName()).log(Level.SEVERE, null, ex);
                }
                double[] realInputs = new double[784];
                for(int i=0;i<realInputs.length;i++){
                    double r = (inputList[i]>>16)&0xff;
                    double g = (inputList[i]>>8)&0xff;
                    double b = (inputList[i])&0xff;
                    realInputs[i] = 255-((r+g+b)/3);
                }
                double[][] outputs = net.query(realInputs);
                double max = -1;
                int maxIndex = -1;
                for(int i=0;i<outputs.length;i++){
                    if(outputs[i][0]>max){
                        max = outputs[i][0];
                        maxIndex = i;
                    }
                }
                field.setText(maxIndex+"");
            }
        }
    };
    
    public static String directory(int i){
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("Search for Weights File");
        if(i==1){
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        }else{
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        } 
        chooser.setAcceptAllFileFilterUsed(false);

        if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
            return ""+chooser.getSelectedFile();
        }else{
            JOptionPane.showMessageDialog(null, "No file chosen");
            System.exit(0);
        }
        return null;
    }
    
    public static void main(String[] args) throws FileNotFoundException, IOException{
        double[][] wih = new double[100][784];
        double[][] who = new double[10][100];
        String loc = directory(1);
        BufferedReader reader = new BufferedReader(new FileReader(loc));
        int count = 0;
        String newLine  = reader.readLine();
        while(!newLine.equals("/**/")){
            String[] splitArray = newLine.split(",");
            for(int i=0;i<wih[count].length;i++){
                wih[count][i] = Double.parseDouble(splitArray[i]);
            }
            count++;
            newLine = reader.readLine();
        }
        count = 0;
        while(reader.ready()){
            newLine = reader.readLine();
            String[] splitArray = newLine.split(",");
            for(int i=0;i<who[count].length;i++){
                who[count][i] = Double.parseDouble(splitArray[i]);
            }
            count++;
        }
        reader.close();
        net = new OCRNeuralNet(784,100,10,0.3,wih,who);
        new SwingPaint().show();
    }
    
    public void show(){
        JFrame frame = new JFrame("Swing Paint");
        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());
        drawArea = new DrawArea();
        
        content.add(drawArea,BorderLayout.CENTER);
        
        JPanel controls = new JPanel();
        controls.setLayout(new BorderLayout());
        
        clearBtn = new JButton("Clear");
        queryBtn = new JButton("Query Neural Net");
        clearBtn.addActionListener(actionListener);
        queryBtn.addActionListener(actionListener);
        
        field = new JTextField("",5);
        
        controls.add(clearBtn,BorderLayout.WEST);
        controls.add(queryBtn,BorderLayout.CENTER);
        controls.add(field,BorderLayout.EAST);
        content.add(controls,BorderLayout.NORTH);
        
        JPanel controlsSouth = new JPanel();
        controlsSouth.setLayout(new BorderLayout());
        
        train = new JButton("Train");
        train.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                if(field.getText().equals("")){
                    JOptionPane.showMessageDialog(null, "Must have target value");
                }else if(Integer.parseInt(field.getText())<0||Integer.parseInt(field.getText())>9){
                    JOptionPane.showMessageDialog(null, "Value must be in range 0-9 (inclusive)");
                }else{
                    Image image = drawArea.getImage().getScaledInstance(28, 28, Image.SCALE_SMOOTH);
                    int[] inputList = new int[784];
                    PixelGrabber grabber = new PixelGrabber(image, 0, 0, 28, 28, inputList, 0, 28);
                    try {
                        grabber.grabPixels();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(SwingPaint.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    double[] realInputs = new double[784];
                    for(int i=0;i<realInputs.length;i++){
                        double r = (inputList[i]>>16)&0xff;
                        double g = (inputList[i]>>8)&0xff;
                        double b = (inputList[i])&0xff;
                        realInputs[i] = 255-((r+g+b)/3);
                    }
                    double[] targetList = {0.01,0.01,0.01,0.01,0.01,0.01,0.01,0.01,0.01,0.01};
                    targetList[Integer.parseInt(field.getText())] = 0.99;
                    net.train(realInputs, targetList);
                    count++;
                    JOptionPane.showMessageDialog(null, "Successfully Trained with New Data!");
                }
            }
        });
        save = new JButton("Save new");
        save.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                if(count>0){
                    try {
                    FileWriter writer = new FileWriter(new File("/Users/vasan10591/Documents/Weights.txt"));
                    for(int i=0;i<net.wih.length;i++){
                        for(int j=0;j<net.wih[i].length;j++){
                            if(j!=net.wih[i].length-1){
                                writer.append(net.wih[i][j]+",");
                            }else{
                                writer.append(net.wih[i][j]+"");
                            }
                        }
                    writer.append("\n");
                    }
                    writer.append("/**/\n");
                    for(int i=0;i<net.who.length;i++){
                        for(int j=0;j<net.who[i].length;j++){
                            if(j!=net.who[i].length-1){
                                writer.append(net.who[i][j]+",");
                            }else{
                                writer.append(net.who[i][j]+"");
                            }
                        }
                        writer.append("\n");
                    }
                    writer.flush();
                    writer.close();
                    } catch (IOException ex) {
                        Logger.getLogger(SwingPaint.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    JOptionPane.showMessageDialog(null, "Successfully saved!");
                    count=0;
                }else{
                    JOptionPane.showMessageDialog(null, "No new edits to the network. Nothing to save");
                }
            }
        });
        viewReverse = new JButton("Reverse");
        viewReverse.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                if(field.getText().equals("")){
                    JOptionPane.showMessageDialog(null, "Must have a target value");
                }else if(Integer.parseInt(field.getText())<0||Integer.parseInt(field.getText())>9){
                    JOptionPane.showMessageDialog(null, "Enter a number between 0-9");
                }else{
                    double[][] targetList = {{0.01,0.01,0.01,0.01,0.01,0.01,0.01,0.01,0.01,0.01}};
                    targetList[0][Integer.parseInt(field.getText())] = 0.99;
                    double[][] imageList = net.reverse(targetList);
                    int[] newList = new int[imageList.length*imageList[0].length];
                    int count=0;
                    for(int i=0;i<imageList.length;i++){
                        for(int j=0;j<imageList[i].length;j++){
                            newList[count] = ((255-(int)imageList[i][j])<<16) | ((255-(int)imageList[i][j])<<8) | ((255-(int)imageList[i][j]));
                            count++;
                        }
                    }
                    try {
                        BufferedImage img = new BufferedImage(28,28,BufferedImage.TYPE_INT_RGB);
                        img.setRGB(0, 0, 28, 28, newList, 0, 28);
                        JOptionPane.showMessageDialog(null, "Choose location to save image");
                        ImageIO.write(img, "jpg", new File(directory(0)+"/imageReverse.jpg"));
                        JOptionPane.showMessageDialog(null, "Done!");
                    } catch (IOException ex) {
                        Logger.getLogger(SwingPaint.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        
        controlsSouth.add(viewReverse,BorderLayout.CENTER);
        controlsSouth.add(train, BorderLayout.WEST);
        controlsSouth.add(save, BorderLayout.EAST);
        content.add(controlsSouth,BorderLayout.SOUTH);
        
        frame.setSize(300,300);
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        frame.setVisible(true);
    }
}