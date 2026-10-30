import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class DataStructureGUI extends JFrame {

    private JPanel displayPanel;
    private JTextArea logArea;
    private JComboBox<String> selector;
    private JButton actionBtn1, actionBtn2, sortBtn;
    
    // Data Structure State
    private List<Integer> dataList = new ArrayList<>();
    private String currentMode = "Stack";

    public DataStructureGUI() {
        setTitle("Data Structures & Sorting Visualizer");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // --- TOP CONTROL PANEL ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        topPanel.setBackground(new Color(240, 240, 245));

        topPanel.add(new JLabel("Select Topic:"));
        String[] options = {
            "Stack", "Queue", "Linked List", "ArrayList",
            "Bubble Sort", "Selection Sort", "Insertion Sort", "Merge Sort", "Quick Sort"
        };
        selector = new JComboBox<>(options);
        topPanel.add(selector);

        actionBtn1 = new JButton("Push / Enqueue / Add");
        actionBtn2 = new JButton("Pop / Dequeue / Remove");
        sortBtn = new JButton("Start Animated Sort");
        sortBtn.setVisible(false);

        topPanel.add(actionBtn1);
        topPanel.add(actionBtn2);
        topPanel.add(sortBtn);

        add(topPanel, BorderLayout.NORTH);

        // --- CENTER VISUALIZATION PANEL ---
        displayPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawVisualization(g);
            }
        };
        displayPanel.setBackground(Color.WHITE);
        add(displayPanel, BorderLayout.CENTER);

        // --- BOTTOM LOGGING PANEL ---
        logArea = new JTextArea(6, 80);
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Console Output & Steps"));
        add(scrollPane, BorderLayout.SOUTH);

        // --- EVENT HANDLERS ---
        selector.addActionListener(e -> updateMode());
        actionBtn1.addActionListener(e -> handleAdd());
        actionBtn2.addActionListener(e -> handleRemove());
        sortBtn.addActionListener(e -> handleSort());

        // Initialize default view
        resetSampleData();
        updateMode();
    }

    private void updateMode() {
        currentMode = (String) selector.getSelectedItem();
        logArea.setText("Switched mode to: " + currentMode + "\n");

        if (currentMode.contains("Sort")) {
            actionBtn1.setText("Generate Random Array");
            actionBtn2.setVisible(false);
            sortBtn.setVisible(true);
            resetSampleData();
        } else {
            actionBtn1.setVisible(true);
            actionBtn2.setVisible(true);
            sortBtn.setVisible(false);

            if (currentMode.equals("Stack")) {
                actionBtn1.setText("Push");
                actionBtn2.setText("Pop");
            } else if (currentMode.equals("Queue")) {
                actionBtn1.setText("Enqueue");
                actionBtn2.setText("Dequeue");
            } else {
                actionBtn1.setText("Add Item");
                actionBtn2.setText("Remove Item");
            }
        }
        displayPanel.repaint();
    }

    private void resetSampleData() {
        dataList.clear();
        if (currentMode.contains("Sort")) {
            int[] samples = {64, 34, 25, 12, 22, 11, 90};
            for (int val : samples) dataList.add(val);
        } else {
            dataList.add(10);
            dataList.add(20);
            dataList.add(30);
        }
    }

    private void handleAdd() {
        if (currentMode.contains("Sort")) {
            dataList.clear();
            for (int i = 0; i < 8; i++) {
                dataList.add((int) (Math.random() * 80) + 10);
            }
            logArea.append("Generated new array: " + dataList + "\n");
        } else {
            String input = JOptionPane.showInputDialog(this, "Enter integer value to add:");
            if (input != null && !input.trim().isEmpty()) {
                try {
                    int val = Integer.parseInt(input.trim());
                    dataList.add(val);
                    logArea.append("Added: " + val + "\n");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid integer.");
                }
            }
        }
        displayPanel.repaint();
    }

    private void handleRemove() {
        if (dataList.isEmpty()) {
            logArea.append("Structure is empty!\n");
            return;
        }

        int removed = -1;
        if (currentMode.equals("Stack")) {
            removed = dataList.remove(dataList.size() - 1); // LIFO
        } else if (currentMode.equals("Queue")) {
            removed = dataList.remove(0); // FIFO
        } else {
            removed = dataList.remove(dataList.size() - 1);
        }
        logArea.append("Removed: " + removed + "\n");
        displayPanel.repaint();
    }

    // --- ANIMATED SORTING IN SEPARATE THREAD ---
    private void handleSort() {
        new Thread(() -> {
            actionBtn1.setEnabled(false);
            sortBtn.setEnabled(false);

            logArea.append("Starting " + currentMode + "...\n");
            int[] arr = dataList.stream().mapToInt(i -> i).toArray();

            switch (currentMode) {
                case "Bubble Sort": animatedBubbleSort(arr); break;
                case "Selection Sort": animatedSelectionSort(arr); break;
                case "Insertion Sort": animatedInsertionSort(arr); break;
                case "Merge Sort": animatedMergeSort(arr, 0, arr.length - 1); break;
                case "Quick Sort": animatedQuickSort(arr, 0, arr.length - 1); break;
            }

            for (int i = 0; i < arr.length; i++) dataList.set(i, arr[i]);
            logArea.append(currentMode + " Completed!\n");

            actionBtn1.setEnabled(true);
            sortBtn.setEnabled(true);
            displayPanel.repaint();
        }).start();
    }

    private void renderStep(int[] arr, String stepInfo) {
        for (int i = 0; i < arr.length; i++) dataList.set(i, arr[i]);
        logArea.append(stepInfo + "\n");
        displayPanel.repaint();
        try {
            Thread.sleep(600); // Animation delay in milliseconds
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // --- SORTING ALGORITHMS WITH STEP VISUALIZATION ---
    private void animatedBubbleSort(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                    renderStep(arr, "Swapped " + arr[j + 1] + " and " + arr[j]);
                }
            }
        }
    }

    private void animatedSelectionSort(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < n; j++) {
                if (arr[j] < arr[minIdx]) minIdx = j;
            }
            int temp = arr[minIdx];
            arr[minIdx] = arr[i];
            arr[i] = temp;
            renderStep(arr, "Placed smallest element " + arr[i] + " at index " + i);
        }
    }

    private void animatedInsertionSort(int[] arr) {
        int n = arr.length;
        for (int i = 1; i < n; ++i) {
            int key = arr[i];
            int j = i - 1;
            while (j >= 0 && arr[j] > key) {
                arr[j + 1] = arr[j];
                j = j - 1;
            }
            arr[j + 1] = key;
            renderStep(arr, "Inserted " + key + " into position " + (j + 1));
        }
    }

    private void animatedMergeSort(int[] arr, int l, int r) {
        if (l < r) {
            int m = l + (r - l) / 2;
            animatedMergeSort(arr, l, m);
            animatedMergeSort(arr, m + 1, r);
            
            // Merge logic with visual rendering
            int n1 = m - l + 1, n2 = r - m;
            int[] L = new int[n1], R = new int[n2];
            for (int i = 0; i < n1; ++i) L[i] = arr[l + i];
            for (int j = 0; j < n2; ++j) R[j] = arr[m + 1 + j];
            int i = 0, j = 0, k = l;
            while (i < n1 && j < n2) {
                if (L[i] <= R[j]) arr[k++] = L[i++];
                else arr[k++] = R[j++];
            }
            while (i < n1) arr[k++] = L[i++];
            while (j < n2) arr[k++] = R[j++];
            renderStep(arr, "Merged subarray from index " + l + " to " + r);
        }
    }

    private void animatedQuickSort(int[] arr, int low, int high) {
        if (low < high) {
            int pivot = arr[high];
            int i = (low - 1);
            for (int j = low; j < high; j++) {
                if (arr[j] <= pivot) {
                    i++;
                    int temp = arr[i]; arr[i] = arr[j]; arr[j] = temp;
                }
            }
            int temp = arr[i + 1]; arr[i + 1] = arr[high]; arr[high] = temp;
            int pi = i + 1;
            renderStep(arr, "Partitioned around pivot " + pivot);

            animatedQuickSort(arr, low, pi - 1);
            animatedQuickSort(arr, pi + 1, high);
        }
    }

    // --- DRAW GRAPHICAL REPRESENTATIONS ---
    private void drawVisualization(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (dataList.isEmpty()) {
            g2.setFont(new Font("SansSerif", Font.BOLD, 16));
            g2.setColor(Color.GRAY);
            g2.drawString("Structure is empty.", 50, 100);
            return;
        }

        int x = 50, y = 150;

        if (currentMode.contains("Sort")) {
            // Draw Bar Graph for Sorting
            int barWidth = 40;
            for (int i = 0; i < dataList.size(); i++) {
                int height = dataList.get(i) * 3;
                g2.setColor(new Color(100, 149, 237));
                g2.fillRect(x + (i * (barWidth + 10)), 300 - height, barWidth, height);
                g2.setColor(Color.BLACK);
                g2.drawRect(x + (i * (barWidth + 10)), 300 - height, barWidth, height);
                g2.drawString(String.valueOf(dataList.get(i)), x + (i * (barWidth + 10)) + 12, 320);
            }
        } else if (currentMode.equals("Linked List")) {
            // Draw Nodes connected by Arrows
            for (int i = 0; i < dataList.size(); i++) {
                g2.setColor(new Color(255, 182, 193));
                g2.fillOval(x, y, 50, 50);
                g2.setColor(Color.BLACK);
                g2.drawOval(x, y, 50, 50);
                g2.drawString(String.valueOf(dataList.get(i)), x + 18, y + 28);

                if (i < dataList.size() - 1) {
                    g2.drawLine(x + 50, y + 25, x + 90, y + 25);
                    g2.drawString(">", x + 85, y + 29); // Arrow head
                }
                x += 90;
            }
        } else {
            // Draw Stack / Queue / ArrayList as Boxes
            for (int i = 0; i < dataList.size(); i++) {
                g2.setColor(new Color(144, 238, 144));
                g2.fillRect(x, y, 60, 40);
                g2.setColor(Color.BLACK);
                g2.drawRect(x, y, 60, 40);
                g2.drawString(String.valueOf(dataList.get(i)), x + 22, y + 25);
                x += 70;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DataStructureGUI().setVisible(true);
        });
    }
}