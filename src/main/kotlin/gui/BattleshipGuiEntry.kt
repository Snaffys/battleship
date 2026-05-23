package gui

import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Font
import javax.swing.BorderFactory
import javax.swing.DefaultListModel
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JList
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextArea
import javax.swing.JTextField
import javax.swing.SwingUtilities

fun runBattleshipGui() {
    SwingUtilities.invokeLater { BattleshipGuiScreen().show() }
}

private class BattleshipGuiScreen(
    private val playerService: application.PlayerService = application.PlayerService(),
    private val controller: BattleshipGuiController = BattleshipGuiController(playerService),
    private val administrationService: application.GameAdministrationService =
        application.GameAdministrationService(playerService),
) {
    private val frame = JFrame("Battleship")
    private val playersModel = DefaultListModel<String>()
    private val playersList = JList(playersModel)
    private val logArea = JTextArea()
    private val filePathField = JTextField("steps/fullAdminSteps.txt")
    private val processFileButton = JButton("Process steps file")

    fun show() {
        buildUi()
        refreshPlayers()
        frame.isVisible = true
    }

    private fun buildUi() {
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.minimumSize = Dimension(960, 800)
        frame.layout = BorderLayout()

        playersList.font = Font(Font.SANS_SERIF, Font.PLAIN, 34)
        logArea.font = Font(Font.MONOSPACED, Font.PLAIN, 30)

        frame.add(buildLeft(), BorderLayout.WEST)
        frame.add(buildRight(), BorderLayout.CENTER)
    }

    private fun buildLeft(): JPanel {
        val rootPanel = JPanel(BorderLayout())
        rootPanel.preferredSize = Dimension(340, 100)

        rootPanel.add(buildAddPlayerPanel(), BorderLayout.NORTH)
        rootPanel.add(JScrollPane(playersList), BorderLayout.CENTER)
        rootPanel.add(buildStepsFilePanel(), BorderLayout.SOUTH)

        return rootPanel
    }

    private fun buildAddPlayerPanel(): JPanel {
        val addPanel = JPanel(BorderLayout())
        val inputField = JTextField()
        val addBtn = JButton("Add player")

        inputField.font = Font(Font.SANS_SERIF, Font.PLAIN, 28)
        addBtn.font = Font(Font.SANS_SERIF, Font.BOLD, 28)

        addPanel.add(inputField, BorderLayout.CENTER)
        addPanel.add(addBtn, BorderLayout.EAST)

        addBtn.addActionListener {
            val name = inputField.text.trim()
            if (name.isEmpty()) {
                showError("Nickname is empty")
            } else {
                controller.addPlayer(name)
                inputField.text = ""
                refreshPlayers()
            }
        }

        return addPanel
    }

    private fun buildStepsFilePanel(): JPanel {
        val filePanel = JPanel(BorderLayout())
        val fileLabel = javax.swing.JLabel("Steps file:")

        fileLabel.font = Font(Font.SANS_SERIF, Font.BOLD, 32)
        filePathField.font = Font(Font.MONOSPACED, Font.PLAIN, 20)

        processFileButton.font = Font(Font.SANS_SERIF, Font.BOLD, 32)
        processFileButton.addActionListener { processStepsFile() }

        filePanel.add(fileLabel, BorderLayout.NORTH)
        filePanel.add(filePathField, BorderLayout.CENTER)
        filePanel.add(processFileButton, BorderLayout.SOUTH)

        return filePanel
    }

    private fun buildRight(): JPanel {
        val panel = JPanel(BorderLayout())
        panel.border = BorderFactory.createEmptyBorder(8, 8, 8, 8)

        val title = javax.swing.JLabel("Processed moves and events")
        title.font = Font(Font.SANS_SERIF, Font.BOLD, 32)

        logArea.isEditable = false
        logArea.lineWrap = true
        logArea.wrapStyleWord = true

        panel.add(title, BorderLayout.NORTH)
        panel.add(buildLogScrollPane(), BorderLayout.CENTER)

        return panel
    }

    private fun buildLogScrollPane(): JScrollPane {
        return JScrollPane(
            logArea,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER,
        ).apply {
            preferredSize = Dimension(560, 560)
            minimumSize = Dimension(560, 400)
        }
    }

    private fun processStepsFile() {
        val path = filePathField.text.trim()
        if (path.isEmpty()) {
            showError("Steps file path is empty")
            return
        }

        val result = administrationService.processStepsFile(path)
        logArea.text = result.logLines.joinToString(separator = "\n")
        refreshPlayers()
    }

    private fun refreshPlayers() {
        val players = controller.getPlayers()
        playersModel.clear()
        for (player in players) {
            playersModel.addElement(player.nickname)
        }
    }

    private fun showError(message: String) {
        JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE)
    }
}
