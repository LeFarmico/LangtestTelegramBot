package bot

import org.telegram.telegrambots.meta.api.objects.Update

interface IMessageReceiver {
    
    suspend fun start()
    
    fun isReceiverStarted(): Boolean   
    
    fun stop(): Boolean
    
    fun add(update: Update)
}
