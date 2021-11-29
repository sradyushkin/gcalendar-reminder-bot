package org.sradyushkin.gcrb.schedule

interface EventListener {
    fun processUpdate(event: Event)
}