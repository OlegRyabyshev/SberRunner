package xyz.fcr.sberrunner.view.fragments.main_fragments.adapter

/**
 * Интерфейс обработки нажатий в RecyclerView
 *
 * @author Рябышев Олег on 05.08.2021
 */
interface ItemClickListener {

    /**
     * Метод для получение позиции в RecyclerView, на которую произошло касание.
     * @param position [Int] позиция в RecyclerView
     */
    fun onItemClick(position: Int)
}
