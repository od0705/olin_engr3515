/**
 * defining linked list class, within this define what a node is
 */
class MyLinkedList<T>{
    // define what a node is, and what is before and after it (doubly linked)
    class LinkedNode<T>(val data: T,
                        var next: LinkedNode<T>? = null,
                        var prev: LinkedNode<T>? = null)
    // defining the head and tail in the linked list
    var head: LinkedNode<T>? = null
    var tail: LinkedNode<T>? = null

    /**
     * Adds the element [data] to the front of the linked list.
     * add conditional for empty list
     * set head, set new item to be after head, link
     */
    fun pushFront(data: T){
        val newItem = LinkedNode(data)
        if (head == null){
            head = newItem
            tail = newItem
        }
        else{
            newItem.next = head
            head?.prev = newItem
            head = newItem
        }
    }

    /**
     * Adds the element [data] to the back of the linked list.
     * add conditional for empty list
     * set tail, set new item to be before tail, link
     */
    fun pushBack(data: T){
        val newItem = LinkedNode(data)
        if (tail == null){
            head = newItem
            tail = newItem
        }
        else{
            newItem.prev = tail
            tail?.next = newItem
            tail = newItem
        }
    }

    /**
     * Removes an element from the front of the list. If the list is empty, it is unchanged.
     * @return the value at the front of the list or nil if none exists
     */
    fun popFront(): T?{
        val poppedItem = head?.data
        head = head?.next
        if (head == null){
            tail == null
        }
        else{
            head?.prev = null
        }
        return poppedItem
    }

    /**
     * Removes an element from the back of the list. If the list is empty, it is unchanged.
     * @return the value at the back of the list or nil if none exists
     */
    fun popBack(): T?{
        val poppedItem = tail?.data
        head = tail?.next
        if (tail == null){
            head == null
        }
        else{
            tail?.prev = null
        }
        return poppedItem
    }

    /**
     * @return the value at the front of the list or nil if none exists
     */
    fun peekFront(): T?{
        return head?.data
    }

    /**
     * @return the value at the back of the list or nil if none exists
     */
    fun peekBack(): T?{
        return tail?.data
    }

    /**
     * @return true if the list is empty and false otherwise
     */
    fun isEmpty(): Boolean{
        return head == null
    }

}


/**
 * Using your linked list class as a data structure,
 * create a class that implements the stack
 * abstract data type.
 */
class MyStacks<T>{
    /**
     * stack is first in last out, so we need to just apply that
     * logic to linked list
     */
    val list = MyLinkedList<T>()

    /**
     * Add [data] to the top of the stack
     */
    fun push(data: T) = list.pushFront(data)

    /**
     * Remove the element at the top of the stack.  If the stack is empty, it remains unchanged.
     * @return the value at the top of the stack or nil if none exists
     */
    fun pop() = list.popFront()

    /**
     * @return the value on the top of the stack or nil if none exists
     */
    fun peek(): T?= list.peekFront()

    /**
     * @return true if the list is empty and false otherwise
     */
    fun isEmpty(): Boolean = list.isEmpty()
}

/**
 * Using your linked list class as a data structure,
 * create a class that implements the queue
 * abstract data type.
 */
class MyQueue<T>{
    /**
     * queue is first in first out, so use linked list
     * to implement that
     */
    val queue = MyLinkedList<T>()

    /**
     * Add [data] to the end of the queue.
     */
    fun enqueue(data: T) = queue.pushBack(data)

    /**
     * Remove the element at the front of the queue.  If the queue is empty, it remains unchanged.
     * @return the value at the front of the queue or nil if none exists
     */
    fun dequeue(): T? = queue.popFront()

    /**
     * @return the value at the front of the queue or nil if none exists
     */
    fun peek(): T? = queue.peekFront()

    /**
     * @return true if the queue is empty and false otherwise
     */
    fun isEmpty(): Boolean = queue.isEmpty()
}

/**
 * leetcode valid parentheses challenge:
 * string must include two matching open + closed parentheses
 * to return true
 */
class ValidParentheses(val seq: String) {
    fun isValid(): Boolean {
        // strategy: make string a stack if contains bracket
        val stack = MyStacks<Char>()

        for (char in seq){
            when (char) {
                // when we have a bracket char, add to a stack
                '(', '[', '{' -> stack.push(char)
                ')', ']', '}' -> {
                    // check if no starting bracket, then false
                    if (stack.isEmpty()) return false
                    // look at what is at the top of the stack
                    // assign return value from pop() to be the current char
                    val current = stack.pop()
                    // match up statements
                    if ((char == ')' && current != '(') ||
                        (char == ']' && current != '[') ||
                        (char == '}' && current != '{')) {
                        return false
                    }
                }
                // if char is not any of the brackets, return false
                else -> return false
            }
        }

        return stack.isEmpty()
    }
}

/**
 * unit tests
 */
fun main() {
    // for MyLinkedList
    println("Unit Tests for MyLinkedList: ")

    val list = MyLinkedList<Int>()
    list.pushFront(4)
    list.pushBack(8)

    println(list.peekFront())
    println(list.peekBack())

    println(list.popFront())
    println(list.popBack())

    println(list.isEmpty())

    // for MyStacks
    println("Unit Tests for MyStacks: ")

    val x = MyStacks<Double>()
    x.push(5.0)
    x.push(2.0)
    println(x.peek())
    println(x.pop())
    println(x.peek())
    x.pop()
    println(x.isEmpty())

    // for MyQueue
    println("Unit Tests for MyQueue: ")

    val q = MyQueue<Int>()
    q.enqueue(1)
    q.enqueue(2)
    q.enqueue(3)
    println(q.peek())
    println(q.dequeue())
    println(q.peek())
    println(q.isEmpty())

    // for ValidParentheses
    println("Unit Tests for ValidParentheses: ")
    val test1 = ValidParentheses("()[]{}")
    println(test1.isValid())
    val test2 = ValidParentheses("([)]")
    println(test2.isValid())
    val test3 = ValidParentheses("{[]}")
    println(test3.isValid())
}
