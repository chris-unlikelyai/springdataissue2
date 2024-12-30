package springdataissue

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.runApplication
import org.springframework.context.event.EventListener
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.UUID

@Table
data class Entity(val name: String, @Id val id: UUID? = null)

interface EntityRepository : CrudRepository<Entity, UUID> {
  fun deleteByName(name: String)
}

@SpringBootApplication class SpringDataIssue

@Service
class Startup(private val entityRepository: EntityRepository) {
  @EventListener(ApplicationReadyEvent::class)
  fun onStartup() {
    val name = "foo"

    val entity1 = entityRepository.save(Entity(name = name))
    println("After initial save:                  $entity1")

    val entity2 = entityRepository.findByIdOrNull(entity1.id!!)
    println("Loaded immediately:                  $entity2")

    entityRepository.deleteByName(name = name)

    val entity3 = entityRepository.findByIdOrNull(entity1.id)
    println("Loaded after delete, expecting null: $entity3")

    entityRepository.deleteById(entity1.id)

    val entity4 = entityRepository.findByIdOrNull(entity1.id)
    println("Loaded after delete, expecting null: $entity4")
  }
}

fun main(args: Array<String>) {
  runApplication<SpringDataIssue>(*args)
}
