import java.util.concurrent.atomic.AtomicReference

class MSQueue<E> : Queue<E> {
    private val dummyNode = Node<E>(null)
    private val head: AtomicReference<Node<E>> = AtomicReference(dummyNode)
    private val tail: AtomicReference<Node<E>> = AtomicReference(dummyNode)

    override fun enqueue(element: E) {
        val newNode = Node(element)

        var tailNode: Node<E>
        while (true) {
            tailNode = tail.get()
            val nextNode = tailNode.next.get()
            if (tailNode === tail.get()) {
                if (nextNode == null) {
                    if (tailNode.next.compareAndSet(nextNode, newNode)) {
                        break
                    }
                } else {
                    tail.compareAndSet(tailNode, nextNode)
                }
            }
        }
        tail.compareAndSet(tailNode, newNode)
    }

    override fun dequeue(): E? {
        var result: E?
        while (true) {
            val headNode = head.get()
            val tailNode = tail.get()
            val nextNode = headNode.next.get()
            if (headNode === head.get()) {
                if (headNode === tailNode) {
                    if (nextNode == null) {
                        return null
                    }
                    tail.compareAndSet(tailNode, nextNode)
                } else {
                    result = nextNode?.element
                    if (head.compareAndSet(headNode, nextNode)) {
                        break
                    }
                }
            }
        }
        head.get().element = null
        return result
    }

    // FOR TEST PURPOSE, DO NOT CHANGE IT.
    override fun validate() {
        check(tail.get().next.get() == null) {
            "At the end of the execution, `tail.next` must be `null`"
        }
        check(head.get().element == null) {
            "At the end of the execution, the dummy node shouldn't store an element"
        }
    }

    private class Node<E>(
        var element: E?
    ) {
        val next = AtomicReference<Node<E>?>(null)
    }
}
