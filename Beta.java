import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.sound.sampled.*;
import javax.imageio.ImageIO;
import java.io.*;
import javax.swing.Icon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Image;
class Beta extends JPanel implements ActionListener
{
    int currentPage;
    double x, y;
    String location;
    JFrame sets;
    JPanel panel;
    JButton start, user, previous, next;
    JTextField name;
    File images[];
    backend object;
    void main()
    {
        sets=new JFrame("Homepage");
        sets.setIconImage(Toolkit.getDefaultToolkit().getImage("Logo.jpg"));
        sets.setLayout(null);
        sets.setExtendedState(JFrame.MAXIMIZED_BOTH);
        sets.setResizable(false);
        sets.setVisible(true);
        name=new JTextField();
        name.setText("");
        user=new JButton(new ImageIcon("User.jpg"));
        user.setBounds((sets.getWidth()-75)/2, (sets.getHeight()-75)/4, 75, 75);
        user.setBorder(BorderFactory.createEmptyBorder());
        user.addActionListener(this);
        user.setToolTipText("Profile photo");
        try
        {
            object=new backend(name.getText(), location, user.getIcon());
            location=object.read()[0].split(";")[1];
        }
        catch (Exception e)
        {
        }
        start=new JButton("START");
        start.setBounds((sets.getWidth()-90)/2, (sets.getHeight()-120), 90, 30);
        sets.add(user);
        panel=new JPanel(new GridLayout(3, 3));
        panel.setBounds(100, 150, 225, 225);
        sets.add(panel);
        previous=new JButton("Previous");
        previous.setBounds(100, 400, 90, 30);
        previous.setVisible(false);
        previous.addActionListener(this);
        sets.add(previous);
        next=new JButton("Next");
        next.setBounds(235, 400, 90, 30);
        next.setVisible(false);
        next.addActionListener(this);
        sets.add(next);
        name.setBounds((sets.getWidth()-150)/2, (sets.getHeight()-30)/2, 150, 30);
        name.setFont(new Font("Comic Sans MS", Font.BOLD, 17));
        try
        {
            name.setText(object.read()[0].split(";")[0]);
            BufferedImage img=ImageIO.read(new File(object.read()[0].split(";")[1]));
            int x=img.getWidth(), y=img.getHeight();
            if(x>y)
            {
                y=75*y/x;
                x=75;
            }
            else
            {
                x=75*x/y;
                y=75;
            }
            user.setIcon(new ImageIcon(img.getScaledInstance(x, y, Image.SCALE_SMOOTH)));
        }
        catch(Exception e)
        {
        }
        sets.add(name);
        start.setToolTipText("Start");
        start.setMnemonic(KeyEvent.VK_A);
        sets.add(start);
        start.addActionListener(this);
        sets.revalidate();
        sets.repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if(e.getSource()==start)
            try
            {
                int i;
                String player=name.getText();
                for(i=0; i<player.length(); i++)
                    if(Character.isLetter(player.charAt(i))==false)
                    {
                        i=-1;
                        break;
                    }
                if(i!=-1)
                {
                    sets.setVisible(false);
                    object.startGame(0, name.getText(), location, user.getIcon());
                }
            }
            catch (Exception error)
            {
            }
        else if(e.getSource()==user)
        {
            JFileChooser directory=new JFileChooser();
            directory.setCurrentDirectory(new File("C:/Users/sbs/Documents/Microsoft/Microsoft Office/Microsoft 365/Project/Avatars"));
            directory.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if(directory.showOpenDialog(null)==JFileChooser.APPROVE_OPTION)
            {
                File files[]=directory.getSelectedFile().listFiles();
                images=filter(files);
                currentPage=0;
                display(currentPage);
            }
        }
        else if(e.getSource()==next && (currentPage+1)*9<images.length)
            display(++currentPage);
        else if(e.getSource()==previous && currentPage>0)
            display(--currentPage);
    }

    File[] filter(File files[])
    {
        int count=0;
        if(files==null)
            return new File[0];
        for(int i=0; i<files.length; i++)
        {
            String filename=files[i].getName().toLowerCase();
            if(filename.endsWith(".jpg") || filename.endsWith(".png") || filename.endsWith(".gif") || filename.endsWith(".bmp") || filename.endsWith(".jpeg"))
                count++;
        }
        File images[]=new File[count];
        for(int i=0, index=0; i<files.length; i++)
        {
            String filename=files[i].getName().toLowerCase();
            if(filename.endsWith(".jpg") || filename.endsWith(".png") || filename.endsWith(".gif") || filename.endsWith(".bmp") || filename.endsWith(".jpeg"))
                images[index++]=files[i];
        }
        return images;
    }

    void display(int page)
    {
        int start=page*9, end=Math.min(start+9, images.length), x, y;
        panel.removeAll();
        for(int i=start; i<end; i++)
            try
            {
                BufferedImage img=ImageIO.read(images[i]);
                x=img.getWidth();
                y=img.getHeight();
                if(x>y)
                {
                    y=75*y/x;
                    x=75;
                }
                else
                {
                    x=75*x/y;
                    y=75;
                }
                JLabel imageLabel=new JLabel(new ImageIcon(img.getScaledInstance(x, y, Image.SCALE_SMOOTH)));
                panel.add(imageLabel);
                final BufferedImage finalImg=img;
                final int index=i;
                imageLabel.addMouseListener(new MouseAdapter()
                    {
                        @Override
                        public void mouseClicked(MouseEvent e)
                        {
                            Image scaledImage=finalImg.getScaledInstance(user.getWidth(), user.getHeight(), Image.SCALE_SMOOTH);
                            user.setIcon(new ImageIcon(scaledImage));
                            panel.setVisible(false);
                            previous.setVisible(false);
                            next.setVisible(false);
                            location=images[index].getPath();
                        }
                    });
            }
            catch(Exception error)
            {
            }
        next.setVisible(end<images.length);
        previous.setVisible(start>0);
        sets.revalidate();
        sets.repaint();
    }
}
class backend extends JPanel implements ActionListener, KeyListener, ComponentListener
{
    int pos[]=new int[3];
    int loop1, x1, check1, x2[]=new int[2], y1[]=new int[2], check2, x3[]=new int[11], y2[]=new int[11], points, moves=15, height=420, width=420, x, y, check3, check4;
    String location;
    JTextField notification;
    JTextArea score, opponent;
    JFrame set;
    JButton play, pause, settings, photo, restart, target;
    JToggleButton state;
    JLabel character;
    Clip clip;
    Timer timer;
    backend(String name, String location, Icon img) throws Exception
    {
        timer=new Timer(17, this);
        timer.start();
        setFocusable(true);
        addKeyListener(this);
        y1[1]=(int)(101*Math.random()-250);
        score=new JTextArea(name + "\nHighscore: " + read()[0].split(";")[2] + "\nScore: " + points);
        score.setToolTipText("Scoreboard");
        score.setOpaque(false);
        score.setFocusable(false);
        score.setBounds(280, 85, 138, 68);
        score.setFont(new Font("Kristen ITC", Font.BOLD, 17));
        play=new JButton(new ImageIcon("Play.jpg"));
        play.setToolTipText("Play");
        play.setMnemonic(KeyEvent.VK_P);
        pause=new JButton(new ImageIcon("Pause.jpg"));
        pause.setToolTipText("Pause");
        pause.setMnemonic(KeyEvent.VK_S);
        settings=new JButton(new ImageIcon("Settings.jpg"));
        settings.setToolTipText("Homepage");
        settings.setMnemonic(KeyEvent.VK_C);
        character=new JLabel(new ImageIcon("Character.gif"));
        character.setBounds(0, 0, 50, 120);
        play.setBounds(0, 0, 35, 35);
        pause.setBounds(0, 0, 35, 35);
        settings.setBounds(35, 0, 35, 35);
        pause.addActionListener(this);
        play.addActionListener(this);
        settings.addActionListener(this);
        addComponentListener(this);
        this.setLayout(null);
        this.add(score);
        this.add(pause);
        this.add(settings);
        this.add(character);
        notification=new JTextField();
        notification.setHorizontalAlignment(JTextField.CENTER);
        notification.setBorder(BorderFactory.createEmptyBorder());
        notification.setFont(new Font("sans-serif", Font.BOLD, 17));
        notification.setForeground(Color.RED);
        notification.setOpaque(false);
        add(notification);
        notification.setVisible(false);
        set=new JFrame("Settings");
        set.setLayout(null);
        set.setIconImage(Toolkit.getDefaultToolkit().getImage("Settings.jpg"));
        set.setSize(420, 420);
        state=new JToggleButton("Sound ON");
        state.setBounds(160, 195, 100, 30);
        set.add(state);
        state.addActionListener(this);
        notification.setBorder(BorderFactory.createEmptyBorder());
        photo=new JButton();
        photo.setIcon(img);
        photo.setBorder(BorderFactory.createEmptyBorder());
        add(photo);
        restart=new JButton("RESTART");
        add(restart);
        restart.setVisible(false);
        restart.setToolTipText("Restart");
        restart.addActionListener(this);
        opponent=new JTextArea(find(0).split(";")[0] + "\n" + find(0).split(";")[2]);
        opponent.setOpaque(false);
        opponent.setFocusable(false);
        opponent.setBounds(280, 158, 138, 68);
        opponent.setFont(new Font("Kristen ITC", Font.BOLD, 17));
        opponent.setToolTipText("Target");
        add(opponent);
        this.location=location;
        target=new JButton();
        BufferedImage targetimg=ImageIO.read(new File(find(0).split(";")[1]));
        x=targetimg.getWidth();
        y=targetimg.getHeight();
        if(x>y)
        {
            y=75*y/x;
            x=75;
        }
        else
        {
            x=75*x/y;
            y=75;
        }
        target.setIcon(new ImageIcon(targetimg.getScaledInstance(x, y, Image.SCALE_SMOOTH)));
        target.setBorder(BorderFactory.createEmptyBorder());
        add(target);
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2d=(Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setStroke(new BasicStroke(5));
        g.setColor(new Color(42, 48, 43));
        g2d.drawLine(x, 0, 10, height);
        g2d.drawLine(x+70, 0, y+20, height);
        g2d.drawLine(x*3+80, 0, y+40, height);
        g2d.drawLine(x*3+150, 0, y*2+50, height);
        g2d.drawLine(x*5+160, 0, y*2+70, height);
        g2d.drawLine(x*5+230, 0, y*3+80, height);
        g.setColor(Color.YELLOW);
        for(loop1=0; loop1<5; loop1++)
            g.fillOval(x3[loop1]+12, y2[loop1], 25, 25);
        g.setColor(Color.ORANGE);
        for(; loop1<10; loop1++)
            g.fillOval(x3[loop1]+12, y2[loop1], 25, 25);
        g.setColor(Color.BLUE);
        g.fillOval(x3[10]+12, y2[10], 25, 25);
        g.setColor(Color.BLUE);
        g.setColor(Color.RED);
        g.fillRect(x2[0], y1[0], 50, 50);
        for(; x2[0]==x2[1] && y1[0]==y1[1]; )
            x2[1]=x2[0]=pos[(int)(3*Math.random())];
        g.fillRect(x2[1], y1[1], 50, 50);
    }

    @Override
    public void componentResized(ComponentEvent e)
    {
        height=getHeight();
        width=getWidth();
        x=(width-240)/6;
        y=(width-100)/3;
        pos[0]=x+15;
        pos[1]=pos[0]+80+x*2;
        pos[2]=2*pos[1]-pos[0];
        x1=pos[1];
        for(loop1=0; loop1<2; loop1++)
            x2[loop1]=pos[(int)(3*Math.random())];
        for(loop1=0; loop1<11; loop1++)
            x3[loop1]=pos[(int)(3*Math.random())];
        repaint();
        score.setBounds(width-Math.min(Math.max(score.getText().split("\n")[0].length()*10, score.getText().split("\n")[1].length()*10), 240), 85, Math.min(Math.max(score.getText().split("\n")[0].length()*10, score.getText().split("\n")[1].length()*10), 240), 68);
        notification.setBounds((width-210)/2, (height-210)/4, 210, 25);
        restart.setBounds((width-87)/2, (height-87)/4, 87, 30);
        photo.setBounds(score.getX()+(width-score.getX()-75)/2, 5, 75, 75);
        target.setBounds(score.getX()+(width-score.getX()-75)/2, score.getY()+83, 75, 75);
        opponent.setBounds(score.getX(), score.getY()+168, score.getWidth(), 46);
    }

    @Override
    public void componentHidden(ComponentEvent e)
    {
    }

    @Override
    public void componentShown(ComponentEvent e)
    {
    }

    @Override
    public void componentMoved(ComponentEvent e)
    {
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        for(loop1=0; loop1<2; loop1++)
            y1[loop1]+=moves;
        for(loop1=0; loop1<11; loop1++)
            y2[loop1]+=moves;
        if(y2[10]>-6999 && check3==1)
        {
            check3=0;
            try
            {
                notify("Special ability deactivated");
            }
            catch(Exception b)
            {
            }
        }
        character.setBounds(x1, height-50, 50, 120);
        if(y1[0]>height)
        {
            y1[0]=-50;
            x2[0]=pos[(int)(3*Math.random())];
        }
        else if(y1[1]>height)
        {
            y1[1]=(int)(101*Math.random()-250);
            x2[1]=pos[(int)(3*Math.random())];
        }
        for(loop1=0; loop1<10; loop1++)
            if(y2[loop1]>height)
            {
                y2[loop1]=-50;
                x3[loop1]=pos[(int)(3*Math.random())];
            }
        if(y2[10]>height)
        {
            y2[10]=-10000;
            x3[10]=pos[(int)(3*Math.random())];
        }
        if(((x1==x2[0] && y1[0]>=(height-100)) || (x1==x2[1] && y1[1]>=(height-100))) && (check3==0 || y2[10]>-6999))
        {
            check2=1;
            timer.stop();
            pause.setEnabled(false);
            settings.setEnabled(false);
            try
            {
                play("End.wav");
                notify("GAME OVER");
                restart.setVisible(true);
                int i=0, check=0;
                for(; i<read().length; i++)
                    if(readLine(i).split(";")[0].equals(score.getText().split("\n")[0]))
                    {
                        if(Integer.valueOf(readLine(i).split(";")[2])<points)
                            writeOnLine(i, 1, score.getText().split("\n")[0]+";"+location+";"+points+";"+check4);
                        else
                            writeOnLine(i, 1, score.getText().split("\n")[0]+";"+location+";"+readLine(i).split(";")[2]+";"+check4);
                        check=1;
                    }
                if(check==0)
                    writeOnLine(i, 0, score.getText().split("\n")[0]+";"+location+";"+points+";"+check4);
            }
            catch(Exception a)
            {
            }
        }
        for(loop1=0; loop1<11; loop1++)
            if(x1==x3[loop1] && y2[loop1]>=(height-75))
            {
                if(loop1!=10)
                    y2[loop1]=-50;
                else
                {
                    y2[loop1]=-10000;
                    check3=1;
                    try
                    {
                        notify("Special ability activated");
                    }
                    catch(Exception b)
                    {
                    }
                }
                x3[loop1]=pos[(int)(3*Math.random())];
                if(loop1<5)
                    points+=1;
                else
                    points+=2;
                try
                {
                    play("Coins.wav");
                }
                catch (Exception c)
                {
                }
                score.setText(score.getText().split("\n")[0] + "\n" + score.getText().split("\n")[1] + "\nScore: " + points);
                if(Integer.valueOf(score.getText().split("\n")[1].substring(11))<points)
                    score.setText(score.getText().split("\n")[0] + "\nHighscore: " + points + "\nScore: " + points);
                if(points>=Integer.valueOf(opponent.getText().split("\n")[1]))
                    if(Integer.valueOf(find(points).split(";")[2])>points)
                    {
                        BufferedImage targetimg=null;
                        try
                        {
                            targetimg=ImageIO.read(new File(find(points).split(";")[1]));
                        }
                        catch (IOException ioe)
                        {
                        }
                        int x=targetimg.getWidth(), y=targetimg.getHeight();
                        if(x>y)
                        {
                            y=75*y/x;
                            x=75;
                        }
                        else
                        {
                            x=75*x/y;
                            y=75;
                        }
                        target.setIcon(new ImageIcon(targetimg.getScaledInstance(x, y, Image.SCALE_SMOOTH)));
                        opponent.setText(find(points).split(";")[0].equals(score.getText().split("\n")[0])?"You" + "\n" + find(points).split(";")[2]:find(points).split(";")[0] + "\n" + find(points).split(";")[2]);
                    }
                    else
                    {
                        target.setVisible(false);
                        opponent.setVisible(false);
                    }
                if(points%100==0)
                    moves+=1;
                if((points/250)%2==0)
                    setBackground(Color.WHITE);
                else
                    setBackground(new Color(0, 1, 0));
            }
        repaint();
        if(e.getSource()==pause)
        {
            check2=1;
            timer.stop();
            this.add(play);
            this.remove(pause);
        }
        else if(e.getSource()==play)
        {
            check2=0;
            timer.start();
            this.add(pause);
            this.remove(play);
            this.requestFocusInWindow();
        }
        else if(e.getSource()==settings)
        {
            pause.doClick();
            set.setVisible(true);
        }
        else if(e.getSource()==state)
            if(state.isSelected())
            {
                state.setText("Sound OFF");
                check4=1;
            }
            else
            {
                state.setText("Sound ON");
                check4=0;
            }
        else if(e.getSource()==restart)
        {
            points=0;
            try
            {
                score.setText(score.getText().split("\n")[0] + "\nHighscore: " + read()[0].split(";")[2] + "\nScore: " + points);
                startGame(1, score.getText().split("\n")[0], location, photo.getIcon());
            }
            catch(Exception a)
            {
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        int keyCode=e.getKeyCode();
        if(keyCode==KeyEvent.VK_LEFT && (check1==0 || check1==1))
        {
            x1-=(pos[1]-pos[0]);
            check1--;
        }
        else if(keyCode==KeyEvent.VK_RIGHT && (check1==-1 || check1==0))
        {
            x1+=(pos[1]-pos[0]);
            check1++;
        }
        if(check2==0)
            repaint();
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
    }

    @Override
    public void keyTyped(KeyEvent e)
    {
    }

    void startGame(int check, String name, String location, Icon img) throws Exception
    {
        if(check==1)
        {
            JFrame previous=(JFrame)SwingUtilities.getWindowAncestor(this);
            previous.dispose();
        }
        JFrame jf=new JFrame("Infinity Run");
        jf.setExtendedState(JFrame.MAXIMIZED_BOTH);
        jf.setResizable(false);
        jf.setIconImage(Toolkit.getDefaultToolkit().getImage("Logo.jpg"));
        backend panel=new backend(name, location, img);
        jf.add(panel);
        jf.setVisible(true);
        jf.setDefaultCloseOperation(jf.EXIT_ON_CLOSE);
        if(check==1)
            timer=new Timer(1, this);
        timer.start();
        play("Start.wav");
    }

    void notify(String message) throws Exception
    {
        notification.setText(message);
        notification.setVisible(true);
        Timer timer=new Timer(3000, new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        notification.setVisible(false);
                        repaint();
                    }
                });
        timer.start();
        if(message.equals("GAME OVER")==false)
            play("Message.wav");
    }

    void play(String file) throws Exception
    {
        if(check4==0)
        {
            File f=new File(file);
            AudioInputStream ais=AudioSystem.getAudioInputStream(f);
            clip=AudioSystem.getClip();
            clip.open(ais);
            clip.start();
        }
    }

    void writeOnLine(int line, int replace, String text) throws Exception
    {
        String x[]=read();
        if(replace==0)
        {
            x=increase(x);
            for(int i=x.length-1; i>=0; i--)
                if(i==line)
                {
                    x[i]=text;
                    break;
                }
                else
                    x[i]=x[i-1];
        }
        x[line]=text;
        File f=new File("Experiment.txt");
        FileWriter fw=new FileWriter(f);
        BufferedWriter bw=new BufferedWriter(fw);
        for(int i=0; i<x.length; i++)
            bw.write(x[i]+"\n");
        bw.close();
    }

    String[] read() throws Exception
    {
        String text="", x[]=new String[1];
        x[0]="";
        File f=new File("Experiment.txt");
        FileReader fr=new FileReader(f);
        BufferedReader br=new BufferedReader(fr);
        while(true)
        {
            text=br.readLine();
            if(text==null)
                break;
            if(x[0]=="")
                x[0]=text;
            else
            {
                x=increase(x);
                x[x.length-1]=text;
            }
        }
        br.close();
        return x;
    }

    String find(int points)
    {
        int i, j;
        String names[]=new String[1];
        try
        {
            names=read();
        }
        catch (Exception e)
        {
        }
        for(i=0, j=0; i<names.length; i++)
            if(Integer.valueOf(names[i].split(";")[2])-points<Integer.valueOf(names[j].split(";")[2])-points && (Integer.valueOf(names[i].split(";")[2])-points)>0)
                j=i;
        return names[j];
    }

    String readLine(int line)
    {
        try
        {
            return read()[line];
        }
        catch (Exception e)
        {
        }
        return "";
    }

    String[] increase(String array[])
    {
        String x[]=new String[array.length+1];
        for(int i=0; i<array.length; i++)
            x[i]=array[i];
        x[x.length-1]="";
        return x;
    }
}
class Data implements Serializable
{
    int highscore;
    String player;
}