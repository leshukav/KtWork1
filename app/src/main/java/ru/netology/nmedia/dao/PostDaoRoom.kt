package ru.netology.nmedia.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ru.netology.nmedia.entity.PostEntity

@Dao
interface PostDaoRoom {
    @Query("SELECT * FROM PostEntity ORDER BY id DESC")
    fun get(): LiveData<List<PostEntity>>

    @Query("SELECT * FROM PostEntity ORDER BY id DESC LIMIT 1")
    fun getLastPost(): PostEntity

    @Insert
    fun insert(post: PostEntity)

    @Query("UPDATE PostEntity SET content = :content WHERE id = :id")
    fun updateContentById(id: Long, content: String)

    fun save(post: PostEntity) =
        if (post.id == 0L) insert(post) else updateContentById(post.id, post.content)

    @Query(
        """
        UPDATE PostEntity SET
        `like` = `like` + CASE WHEN likeByMe THEN -1 ELSE 1 END,
        likeByMe = CASE WHEN likeByMe THEN 0 ELSE 1 END
        WHERE id = :id
        """
    )
    fun likeById(id: Long)

    @Query("DELETE FROM PostEntity WHERE id = :id")
    fun removeById(id: Long)

    @Query(
        """
            UPDATE PostEntity SET  
            share = share + 10 WHERE id = :id 
            """
    )
    fun shareById(id: Long)
}