package cc.mewcraft.worldreset.manager

import com.google.common.cache.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.lucko.helper.terminable.Terminable
import java.io.File
import java.util.UUID

class UserDataManager(
    private val directory: File,
) : Terminable {

    companion object {
        private const val PRE_LOAD_AMOUNT = 1000
    }

    private val directoryOrCreate: File
        get() {
            if (!directory.exists()) {
                directory.mkdirs()
            }
            return directory
        }

    // 使用 Guava LoadingCache 缓存用户数据
    private val cache: LoadingCache<UUID, UserData> = CacheBuilder.newBuilder()
        .removalListener(RemovalListener<UUID, UserData> { notification ->
            // 当缓存中的用户被移除时，将其持久化到文件
            if (notification.wasEvicted()) {
                saveUserToFile(notification.value!!)
            }
        })
        .build(
            // 当缓存中没有该用户时, 从文件加载
            CacheLoader.from(::loadUserFromFile)
        )

    // 按文件的最后更新时间顺序加载前 100 个用户文件
    fun preloadUsers() {
        val files = directoryOrCreate
            .listFiles { f, _ -> f.extension == "json" }
            ?.sortedByDescending { it.lastModified() }
            ?.take(PRE_LOAD_AMOUNT)

        files?.forEach { file ->
            val id = UUID.fromString(file.nameWithoutExtension)
            val userData = loadUserFromFile(id)
            cache.put(id, userData)
        }
    }

    // 从文件加载或创建新的用户数据
    private fun loadUserFromFile(id: UUID): UserData {
        val userFile = File(directoryOrCreate, "$id.json")
        if (userFile.exists()) {
            val jsonString = userFile.readText()
            return Json.decodeFromString(jsonString)
        } else {
            // 文件不存在时, 创建一个新的 UserData
            val newUserData = UserData(id, false)
            // 将新用户数据保存到文件
            saveUserToFile(newUserData)
            return newUserData
        }
    }

    // 保存用户数据到文件
    private fun saveUserToFile(userData: UserData) {
        val userFile = File(directoryOrCreate, "${userData.id}.json")
        val jsonString = Json.encodeToString(userData)
        userFile.writeText(jsonString)
    }

    // 将所有缓存中的用户数据写回文件
    private fun saveEachUserToFile() {
        cache.asMap().values.forEach { userData ->
            saveUserToFile(userData)
        }
    }

    // 获取用户数据
    fun getUser(id: UUID): UserData {
        // 从缓存中获取, 如果没有则从文件加载
        return cache.get(id)
    }

    // 修改用户数据
    fun modifyUser(id: UUID, modify: (UserData) -> UserData) {
        val userData = getUser(id)
        val newUserData = modify(userData)
        cache.put(id, newUserData)
    }

    // 修改所有用户数据
    fun modifyEachUser(modify: (UserData) -> UserData) {
        cache.asMap().forEach { (id, userData) ->
            val newUserData = modify(userData)
            cache.put(id, newUserData)
        }
    }

    // 删除用户数据
    fun deleteUser(id: UUID): Boolean {
        if (cache.getIfPresent(id) != null) {
            // 从缓存中移除
            cache.invalidate(id)
            val userFile = File(directoryOrCreate, "$id.json")
            if (userFile.exists()) {
                // 从文件系统中删除
                userFile.delete()
            }
            return true
        } else {
            return false
        }
    }

    // 获取所有用户数据
    fun getAllUsers(): Collection<UserData> {
        // 获取缓存中的所有用户
        return cache.asMap().values
    }

    override fun close() {
        // 关闭时将所有用户数据写回文件
        saveEachUserToFile()
    }
}

