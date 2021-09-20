package pers.kaoru.rfs.client.ui.control;

import pers.kaoru.rfs.client.BitCount;
import pers.kaoru.rfs.client.transmission.TaskRecord;
import pers.kaoru.rfs.client.transmission.TaskState;

import javax.swing.*;

public class TaskView {

    private final TaskRecord record;

    private final JLabel uidLabel = new JLabel();
    private final JLabel nameLabel = new JLabel();
    private final JLabel typeLabel = new JLabel();
    private final JProgressBar progressBar = new JProgressBar(0, 100);
    private final JLabel fractionLabel = new JLabel();
    private final JLabel speedLabel = new JLabel();
    private final JLabel stateLabel = new JLabel();

    public TaskView(TaskRecord record) {
        this.record = record;

        uidLabel.setText(record.getUid());
        nameLabel.setText(record.getName());
        typeLabel.setText(record.getType().name());
        progressBar.setStringPainted(true);

        typeLabel.setHorizontalAlignment(JLabel.RIGHT);
        fractionLabel.setHorizontalAlignment(JLabel.RIGHT);
        speedLabel.setHorizontalAlignment(JLabel.RIGHT);
        stateLabel.setHorizontalAlignment(JLabel.RIGHT);
    }

    public void updateProgress() {
        var v = (float) record.getCurrent() / record.getLength() * 100;
        progressBar.setValue((int) v);
        fractionLabel.setText(BitCount.ToString(record.getCurrent()) + " / " + BitCount.ToString(record.getLength()));
    }

    public void updateSpeed(long speed) {
        speedLabel.setText(BitCount.ToString(speed) + "/S");
    }

    public void setState(TaskState state) {
        stateLabel.setText(state.name());
    }

    public TaskRecord getRecord() {
        return record;
    }

    public JLabel getUidLabel() {
        return uidLabel;
    }

    public JLabel getNameLabel() {
        return nameLabel;
    }

    public JLabel getTypeLabel() {
        return typeLabel;
    }

    public JProgressBar getProgressBar() {
        return progressBar;
    }

    public JLabel getFractionLabel() {
        return fractionLabel;
    }

    public JLabel getSpeedLabel() {
        return speedLabel;
    }

    public JLabel getStateLabel() {
        return stateLabel;
    }
}
