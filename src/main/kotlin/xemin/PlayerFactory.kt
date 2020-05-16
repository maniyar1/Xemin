package xemin

import com.almasb.fxgl.dsl.EntityBuilder
import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.EntityFactory
import com.almasb.fxgl.entity.SpawnData
import com.almasb.fxgl.entity.Spawns
import javafx.geometry.Point2D
import javafx.scene.shape.Rectangle
import xemin.component.PlayerComponent

object PlayerFactory : EntityFactory {

    @Spawns("player")
    fun newPlayer(data: SpawnData): Entity {
       return EntityBuilder().from(data).view(Rectangle(70.0, 70.0)).with(PlayerComponent(Point2D(data.x, data.y))).build()
    }
}