package bot

interface IBot {
    
    val controller: IMessageController
    
    suspend fun connect()
}
