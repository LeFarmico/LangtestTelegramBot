package command

interface ICommandManager {

    fun commandAction(commandWithInfo: CommandWithInfo)
}
