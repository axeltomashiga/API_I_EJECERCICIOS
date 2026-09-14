package ar.edu.uade.axelhiga.ejercicios.data.repository

import ar.edu.uade.axelhiga.ejercicios.domain.model.Materia

class MateriasRepositoryImpl  {
    fun getMaterias() : List<Materia> = listOf(
        Materia("Programacion", 1, true),
        Materia("Matematica", 1, false),
        Materia("Fisica", 1, true),
        Materia("Quimica", 1, false),
        Materia("Historia", 1, true),
        Materia("Lengua", 1, false),
        Materia("Ingles", 1, true),
    )
}