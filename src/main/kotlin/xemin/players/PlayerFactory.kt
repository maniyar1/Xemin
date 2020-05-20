package xemin.players

import com.almasb.fxgl.dsl.EntityBuilder
import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.EntityFactory
import com.almasb.fxgl.entity.SpawnData
import com.almasb.fxgl.entity.Spawns
import com.almasb.fxgl.entity.components.CollidableComponent
import javafx.geometry.Point2D
import javafx.scene.shape.Rectangle
import xemin.EntityType
import xemin.component.PlayerComponent

object PlayerFactory : EntityFactory {

    @Spawns("player")
    fun newPlayer(data: SpawnData): Entity {
       return EntityBuilder().from(data).view(Rectangle(70.0, 70.0)).with(CollidableComponent(true)).with(PlayerComponent(Point2D(data.x, data.y), data.get("id"))).type(EntityType.PLAYER).build()
    }
}