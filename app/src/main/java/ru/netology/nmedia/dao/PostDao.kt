package ru.netology.nmedia.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.entity.PostEntity

@Dao
interface PostDao {
    @Query("SELECT * FROM PostEntity ORDER BY id DESC")
    fun get(): Flow<List<PostEntity>>

    @Query("SELECT * FROM PostEntity WHERE hidden = 0 ORDER BY id DESC")
    fun getAllVisible(): Flow<List<PostEntity>>

    @Query("SELECT * FROM PostEntity ORDER BY id DESC LIMIT 1")
    suspend fun getLastPost(): PostEntity

//    @Query("SELECT * FROM  PostEntity WHERE id = :id")
//    suspend fun getPost(id: Long): Post

    @Query("SELECT COUNT(*) == 0 FROM PostEntity")
    suspend fun isEmpty(): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(posts: List<PostEntity>)

    @Query("DELETE FROM PostEntity WHERE id = :id")
    suspend fun removeById(id: Long)

    @Query("UPDATE PostEntity SET hidden = 0")
    suspend fun readAll()

    @Query("SELECT COUNT(hidden) FROM PostEntity WHERE 1")
    suspend fun getUnreadCount(): Int

    @Query("UPDATE PostEntity SET content = :content WHERE id = :id")
    suspend fun updateContentById(id: Long, content: String)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertHidden(post: List<PostEntity>)

    @Query(
        """
        UPDATE PostEntity SET
        `likes` = `likes` + CASE WHEN likedByMe THEN -1 ELSE 1 END,
        likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END
        WHERE id = :id
        """
    )
   suspend fun likeById(id: Long)


}