import com.example.pipegame.model.AdjacencyListGraph;
import com.example.pipegame.model.AdjacencyMatrixGraph;
import com.example.pipegame.model.Edge;
import com.example.pipegame.model.Vertex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AdjacencyListGraphTest {

    private AdjacencyListGraph<Integer> graph;

    @BeforeEach
    public void setUp() {
        graph = new AdjacencyListGraph<>();
    }

    @Test
    public void testAddVertexStandard() {
        // Escenario estándar
        Vertex<Integer> vertex = new Vertex<>(5);
        graph.addVertex(vertex);
        assertTrue(graph.getVertices().contains(vertex));
    }

    @Test
    public void testAddVertexLimit() {
        // Escenario de límite
        Vertex<Integer> vertex = new Vertex<>(Integer.MAX_VALUE);
        graph.addVertex(vertex);
        Vertex<Integer> foundVertex = graph.findVertex(Integer.MAX_VALUE);
        assertEquals(vertex, foundVertex);
    }

    @Test
    public void testAddVertexInteresting() {
        // Escenario interesante
        Vertex<Integer> vertex1 = new Vertex<>(10);
        Vertex<Integer> vertex2 = new Vertex<>(20);

        graph.addVertex(vertex1);
        graph.addVertex(vertex2);

        // Verificar si se han añadido ambos vértices
        assertTrue(graph.getVertices().contains(vertex1));
        assertTrue(graph.getVertices().contains(vertex2));
    }


    @Test
    public void testFindVertexStandard() {
        // Escenario estándar
        Vertex<Integer> vertex = new Vertex<>(8);
        graph.addVertex(vertex);
        Vertex<Integer> foundVertex = graph.findVertex(8);
        assertEquals(vertex, foundVertex);
    }

    @Test
    public void testFindVertexLimit() {
        // Escenario de límite
        Vertex<Integer> vertex = new Vertex<>(Integer.MIN_VALUE);
        graph.addVertex(vertex);
        Vertex<Integer> foundVertex = graph.findVertex(Integer.MIN_VALUE);
        assertEquals(vertex, foundVertex);
    }

    @Test
    public void testFindVertexInteresting() {
        // Escenario interesante
        Vertex<Integer> vertex = new Vertex<>(15);
        graph.addVertex(vertex);

        // Encontrar el vértice que no existe
        Vertex<Integer> foundVertex = graph.findVertex(100);
        Assertions.assertNull(foundVertex);
    }


    @Test
    public void testRemoveVertexStandard() {
        // Escenario estándar
        Vertex<Integer> vertex = new Vertex<>(3);
        graph.addVertex(vertex);
        graph.removeVertex(vertex);
        Assertions.assertFalse(graph.getVertices().contains(vertex));
    }

    @Test
    public void testRemoveVertexLimit() {
        // Escenario de límite
        Vertex<Integer> vertex = new Vertex<>(Integer.MAX_VALUE);
        graph.addVertex(vertex);
        graph.removeVertex(vertex);
        Assertions.assertFalse(graph.getVertices().contains(vertex));
    }

    @Test
    public void testRemoveVertexInteresting() {
        // Escenario interesante
        Vertex<Integer> vertex1 = new Vertex<>(50);
        Vertex<Integer> vertex2 = new Vertex<>(60);
        graph.addVertex(vertex1);
        graph.addVertex(vertex2);
        graph.addEdge(vertex1, vertex2, 5);

        graph.removeVertex(vertex1);

        // Verificar si el vértice ha sido eliminado correctamente
        Assertions.assertFalse(graph.getVertices().contains(vertex1));
        Assertions.assertNull(graph.findVertex(50)); // Asegurar que no se pueda encontrar el vértice eliminado
        // Verificar si los bordes también se eliminaron
        assertTrue(graph.getVertices().contains(vertex2));
        assertTrue(graph.findVertex(60) != null && graph.findVertex(60).getNeighbors().isEmpty());
    }

    @Test
    public void testAddEdgeStandard() {
        // Escenario estándar
        Vertex<Integer> vertex1 = new Vertex<>(10);
        Vertex<Integer> vertex2 = new Vertex<>(20);
        graph.addVertex(vertex1);
        graph.addVertex(vertex2);

        graph.addEdge(vertex1, vertex2, 7);
        Edge<Integer> edge = graph.findEdge(vertex1, vertex2);
        assertNotNull(edge);
        assertEquals(7, edge.getWeight());
    }

    @Test
    public void testAddEdgeLimit() {
        // Escenario de límite
        Vertex<Integer> vertex1 = new Vertex<>(Integer.MAX_VALUE);
        Vertex<Integer> vertex2 = new Vertex<>(Integer.MIN_VALUE);
        graph.addVertex(vertex1);
        graph.addVertex(vertex2);

        graph.addEdge(vertex1, vertex2, 15);
        Edge<Integer> edge = graph.findEdge(vertex1, vertex2);
        assertNotNull(edge);
        assertEquals(15, edge.getWeight());
    }

    @Test
    public void testAddEdgeInteresting() {
        // Escenario interesante
        Vertex<Integer> vertex1 = new Vertex<>(30);
        Vertex<Integer> vertex2 = new Vertex<>(40);
        Vertex<Integer> vertex3 = new Vertex<>(50);
        graph.addVertex(vertex1);
        graph.addVertex(vertex2);
        graph.addVertex(vertex3);

        graph.addEdge(vertex1, vertex2, 10);
        graph.addEdge(vertex2, vertex3, 5);

        // Verificar si se ha creado correctamente el borde y sus pesos
        Edge<Integer> edge1 = graph.findEdge(vertex1, vertex2);
        assertNotNull(edge1);
        assertEquals(10, edge1.getWeight());

        Edge<Integer> edge2 = graph.findEdge(vertex2, vertex3);
        assertNotNull(edge2);
        assertEquals(5, edge2.getWeight());
    }



    @Test
    public void testRemoveEdgeStandard() {
        AdjacencyListGraph<String> graph = new AdjacencyListGraph<>();
        Vertex<String> vertexA = new Vertex<>("A");
        Vertex<String> vertexB = new Vertex<>("B");

        graph.addVertex(vertexA);
        graph.addVertex(vertexB);
        graph.addEdge(vertexA, vertexB, 5);
        graph.removeEdge(vertexA, vertexB);

        assertNull(graph.findEdge(vertexA, vertexB));
    }

    @Test
    public void testRemoveEdgeEdgeCases() {
        AdjacencyListGraph<Integer> graph = new AdjacencyListGraph<>();
        Vertex<Integer> vertexA = new Vertex<>(1);
        Vertex<Integer> vertexB = new Vertex<>(2);

        graph.addVertex(vertexA);
        graph.addVertex(vertexB);
        graph.removeEdge(vertexA, vertexB); // Eliminar un borde en un grafo sin bordes
    }

    @Test
    public void testRemoveEdgeInteresting() {
        AdjacencyListGraph<String> graph = new AdjacencyListGraph<>();
        Vertex<String> vertexA = new Vertex<>("A");
        Vertex<String> vertexB = new Vertex<>("B");
        Vertex<String> vertexC = new Vertex<>("C");

        graph.addVertex(vertexA);
        graph.addVertex(vertexB);
        graph.addVertex(vertexC);
        graph.addEdge(vertexA, vertexB, 2);
        graph.addEdge(vertexB, vertexC, 4);
        graph.removeEdge(vertexA, vertexB);

        assertNull(graph.findEdge(vertexA, vertexB));
        assertNotNull(graph.findEdge(vertexB, vertexC));
    }


    @Test
    public void testDFSStandard() {
        // Escenario estándar
        Vertex<Integer> vertex1 = new Vertex<>(1);
        Vertex<Integer> vertex2 = new Vertex<>(2);
        Vertex<Integer> vertex3 = new Vertex<>(3);
        Vertex<Integer> vertex4 = new Vertex<>(4);

        graph.addVertex(vertex1);
        graph.addVertex(vertex2);
        graph.addVertex(vertex3);
        graph.addVertex(vertex4);

        graph.addEdge(vertex1, vertex2, 1);
        graph.addEdge(vertex1, vertex3, 1);
        graph.addEdge(vertex2, vertex4, 1);
        graph.addEdge(vertex3, vertex4, 1);

        ArrayList<Vertex<Integer>> dfsOrder = graph.dfs(vertex1);
        List<Integer> expectedOrder = Arrays.asList(1, 2, 4, 3);

        for (int i = 0; i < dfsOrder.size(); i++) {
            assertEquals(expectedOrder.get(i), dfsOrder.get(i).getData());
        }
    }

    @Test
    public void testDFSLimit() {
        // Escenario de límite: grafo vacío

        ArrayList<Vertex<Integer>> dfsOrder = graph.dfs(null);
        assertTrue(dfsOrder.isEmpty());
    }

    @Test
    public void testDFSInteresting() {
        // Escenario interesante: vértices desconectados
        Vertex<Integer> vertex1 = new Vertex<>(1);
        Vertex<Integer> vertex2 = new Vertex<>(2);
        Vertex<Integer> vertex3 = new Vertex<>(3);

        graph.addVertex(vertex1);
        graph.addVertex(vertex2);
        graph.addVertex(vertex3);

        ArrayList<Vertex<Integer>> dfsOrder = graph.dfs(vertex1);
        // Verificar que cada vértice esté en un componente distinto, por lo que el orden debe ser el mismo que el agregado.
        List<Integer> expectedOrder = Arrays.asList(1, 2, 3);

        for (int i = 0; i < dfsOrder.size(); i++) {
            assertEquals(expectedOrder.get(i), dfsOrder.get(i).getData());
        }
    }


    @Test
    public void testBFSStandard() {
        // Escenario estándar
        Vertex<Integer> vertex1 = new Vertex<>(1);
        Vertex<Integer> vertex2 = new Vertex<>(2);
        Vertex<Integer> vertex3 = new Vertex<>(3);
        Vertex<Integer> vertex4 = new Vertex<>(4);

        graph.addVertex(vertex1);
        graph.addVertex(vertex2);
        graph.addVertex(vertex3);
        graph.addVertex(vertex4);

        graph.addEdge(vertex1, vertex2, 1);
        graph.addEdge(vertex1, vertex3, 1);
        graph.addEdge(vertex2, vertex4, 1);
        graph.addEdge(vertex3, vertex4, 1);

        ArrayList<Vertex<Integer>> bfsOrder = graph.bfs(vertex1);
        List<Integer> expectedOrder = Arrays.asList(1, 2, 3, 4);

        for (int i = 0; i < bfsOrder.size(); i++) {
            assertEquals(expectedOrder.get(i), bfsOrder.get(i).getData());
        }
    }

    @Test
    public void testBFSEdgeCases() {
        AdjacencyListGraph<Integer> graph = new AdjacencyListGraph<>();
        Vertex<Integer> vertexA = new Vertex<>(1);

        graph.addVertex(vertexA);
        ArrayList<Vertex<Integer>> bfsOrder = graph.bfs(vertexA);

        assertNotNull(bfsOrder);
        assertEquals(1, bfsOrder.size()); // En un grafo con un solo vértice
        assertEquals(vertexA, bfsOrder.get(0));
    }

    @Test
    public void testBFSInteresting() {
        // Escenario interesante: vértices desconectados
        Vertex<Integer> vertex1 = new Vertex<>(1);
        Vertex<Integer> vertex2 = new Vertex<>(2);
        Vertex<Integer> vertex3 = new Vertex<>(3);

        graph.addVertex(vertex1);
        graph.addVertex(vertex2);
        graph.addVertex(vertex3);

        ArrayList<Vertex<Integer>> bfsOrder = graph.bfs(vertex1);
        // Verificar que cada vértice esté en un componente distinto, por lo que el orden debe ser el mismo que el agregado.
        List<Integer> expectedOrder = Arrays.asList(1, 2, 3);

        for (int i = 0; i < bfsOrder.size(); i++) {
            assertEquals(expectedOrder.get(i), bfsOrder.get(i).getData());
        }
    }

    @Test
    public void testDijkstraStandardCase() {
        // Arrange
        AdjacencyListGraph<Integer> graph = new AdjacencyListGraph<>();
        Vertex<Integer> vertexA = new Vertex<>(1);
        Vertex<Integer> vertexB = new Vertex<>(2);
        Vertex<Integer> vertexC = new Vertex<>(3);
        Vertex<Integer> vertexD = new Vertex<>(4);

        graph.addVertex(vertexA);
        graph.addVertex(vertexB);
        graph.addVertex(vertexC);
        graph.addVertex(vertexD);

        graph.addEdge(vertexA, vertexB, 1);
        graph.addEdge(vertexA, vertexC, 3);
        graph.addEdge(vertexB, vertexD, 2);
        graph.addEdge(vertexC, vertexD, 1);

        // Act
        ArrayList<Vertex<Integer>> shortestPath = graph.dijkstra(vertexA, vertexD);

        // Assert
        assertNotNull(shortestPath);
        assertEquals(3, shortestPath.size());
        assertEquals(vertexD, shortestPath.get(0));
        assertEquals(vertexB, shortestPath.get(1));
        assertEquals(vertexA, shortestPath.get(2));
    }

    @Test
    public void testDijkstraEdgeCases() {
        // Edge case: Empty graph
        AdjacencyListGraph<String> graph = new AdjacencyListGraph<>();
        Vertex<String> startVertex = new Vertex<>("A");
        Vertex<String> endVertex = new Vertex<>("B");

        ArrayList<Vertex<String>> shortestPath = graph.dijkstra(startVertex, endVertex);

        assertNotNull(shortestPath);
        assertEquals(1, shortestPath.size());
    }

    @Test
    public void testDijkstraInterestingCase() {
        // Interesting case: Graph with negative weights
        AdjacencyListGraph<Character> graph = new AdjacencyListGraph<>();
        Vertex<Character> vertexS = new Vertex<>('S');
        Vertex<Character> vertexA = new Vertex<>('A');
        Vertex<Character> vertexB = new Vertex<>('B');

        graph.addVertex(vertexS);
        graph.addVertex(vertexA);
        graph.addVertex(vertexB);

        graph.addEdge(vertexS, vertexA, 10);
        graph.addEdge(vertexS, vertexB, 5);
        graph.addEdge(vertexA, vertexB, -2);

        ArrayList<Vertex<Character>> shortestPath = graph.dijkstra(vertexS, vertexB);

        assertNotNull(shortestPath);
        assertEquals(2, shortestPath.size());
        assertEquals(vertexB, shortestPath.get(0));
        assertEquals(vertexS, shortestPath.get(1));
    }

    @Test
    public void testFloydWarshallStandardCase() {
        // Arrange
        AdjacencyListGraph<Integer> graph = new AdjacencyListGraph<>();
        Vertex<Integer> vertexA = new Vertex<>(1);
        Vertex<Integer> vertexB = new Vertex<>(2);
        Vertex<Integer> vertexC = new Vertex<>(3);

        graph.addVertex(vertexA);
        graph.addVertex(vertexB);
        graph.addVertex(vertexC);

        graph.addEdge(vertexA, vertexB, 3);
        graph.addEdge(vertexA, vertexC, 8);
        graph.addEdge(vertexB, vertexC, 1);

        // Act
        int[][] shortestPaths = graph.floydWarshall();

        // Assert
        assertNotNull(shortestPaths);
        assertEquals(3, shortestPaths.length);
        assertEquals(3, shortestPaths[0].length);
        assertEquals(0, shortestPaths[0][0]); // Distance from A to A
        assertEquals(3, shortestPaths[0][1]); // Distance from A to B
        assertEquals(4, shortestPaths[0][2]); // Distance from A to C
    }

    @Test
    public void testFloydWarshallEdgeCases() {
        // Edge case: Empty graph
        AdjacencyListGraph<String> graph = new AdjacencyListGraph<>();
        int[][] shortestPaths = graph.floydWarshall();

        assertNotNull(shortestPaths);
        assertEquals(0, shortestPaths.length);
    }


    @Test
    public void testFloydWarshallInterestingCase() {
        // Interesting case: Graph with negative cycle
        AdjacencyListGraph<Character> graph = new AdjacencyListGraph<>();
        Vertex<Character> vertexS = new Vertex<>('S');
        Vertex<Character> vertexA = new Vertex<>('A');
        Vertex<Character> vertexB = new Vertex<>('B');

        graph.addVertex(vertexS);
        graph.addVertex(vertexA);
        graph.addVertex(vertexB);

        graph.addEdge(vertexS, vertexA, 5);
        graph.addEdge(vertexA, vertexB, 3);
        graph.addEdge(vertexB, vertexA, -7);

        int[][] shortestPaths = graph.floydWarshall();

        assertNotNull(shortestPaths);
        assertTrue(hasNegativeCycle(shortestPaths));
    }

    private boolean hasNegativeCycle(int[][] distances) {
        int V = distances.length;
        for (int i = 0; i < V; i++) {
            if (distances[i][i] < 0) {
                return true; // There is a negative cycle if any diagonal element is negative
            }
        }
        return false;
    }

    @Test
    public void testPrimStandardCase() {
        // Arrange
        AdjacencyListGraph<Integer> graph = new AdjacencyListGraph<>();
        Vertex<Integer> vertexA = new Vertex<>(1);
        Vertex<Integer> vertexB = new Vertex<>(2);
        Vertex<Integer> vertexC = new Vertex<>(3);

        graph.addVertex(vertexA);
        graph.addVertex(vertexB);
        graph.addVertex(vertexC);

        graph.addEdge(vertexA, vertexB, 3);
        graph.addEdge(vertexA, vertexC, 8);
        graph.addEdge(vertexB, vertexC, 1);

        // Act
        AdjacencyListGraph<Integer> mstGraph = graph.primAL();

        // Assert
        assertNotNull(mstGraph);
        assertEquals(3, mstGraph.getVertices().size()); // MST should have the same number of vertices
        assertEquals(2, countEdges(mstGraph));
    }



    @Test
    public void testPrimEdgeCases() {

        AdjacencyListGraph<Integer> graph = new AdjacencyListGraph<>();
        Vertex<Integer> vertex1 = new Vertex<>(1);
        Vertex<Integer> vertex2 = new Vertex<>(2);

        // Agregar el mínimo número de vértices (dos) y conectarlos
        graph.addVertex(vertex1);
        graph.addVertex(vertex2);
        graph.addEdge(vertex1, vertex2, 1); // Conectar ambos vértices con un peso

        AdjacencyListGraph<Integer> mstGraph = graph.primAL();

        assertNotNull(mstGraph);
        assertEquals(2, mstGraph.getVertices().size());
        assertEquals(1, countEdges(mstGraph)); // El MST tendrá un solo borde en este caso
    }





    @Test
    public void testPrimInterestingCase() {
        // Interesting case: Graph with disconnected components
        AdjacencyListGraph<Character> graph = new AdjacencyListGraph<>();
        Vertex<Character> vertexS = new Vertex<>('S');
        Vertex<Character> vertexA = new Vertex<>('A');
        Vertex<Character> vertexB = new Vertex<>('B');

        graph.addVertex(vertexS);
        graph.addVertex(vertexA);
        graph.addVertex(vertexB);

        graph.addEdge(vertexS, vertexA, 5);
        graph.addEdge(vertexS, vertexB, 7);

        AdjacencyListGraph<Character> mstGraph = graph.primAL();

        assertNotNull(mstGraph);
        assertEquals(3, mstGraph.getVertices().size()); // MST should have all vertices
        assertEquals(2, countEdges(mstGraph)); // MST should have two edges
    }

    // Helper method to count edges in the graph
    private <T> int countEdges(AdjacencyListGraph<T> graph) {
        int edgeCount = 0;
        for (Vertex<T> vertex : graph.getVertices()) {
            edgeCount += vertex.getNeighbors().size();
        }
        // Divide by 2 to avoid counting each edge twice (since they're undirected)
        return edgeCount / 2;
    }

    @Test
    public void testKruskalStandardCase() {
        AdjacencyListGraph<Integer> graph = new AdjacencyListGraph<>();
        Vertex<Integer> vertexA = new Vertex<>(1);
        Vertex<Integer> vertexB = new Vertex<>(2);
        Vertex<Integer> vertexC = new Vertex<>(3);

        graph.addVertex(vertexA);
        graph.addVertex(vertexB);
        graph.addVertex(vertexC);

        graph.addEdge(vertexA, vertexB, 3);
        graph.addEdge(vertexA, vertexC, 8);
        graph.addEdge(vertexB, vertexC, 1);

        AdjacencyListGraph<Integer> mstGraph = graph.kruskalAL();

        assertNotNull(mstGraph);
        assertEquals(3, mstGraph.getVertices().size());
        assertEquals(2, countEdges(mstGraph));

    }

    @Test
    public void testKruskalEmptyGraph() {
        // Arrange
        AdjacencyListGraph<String> graph = new AdjacencyListGraph<>();

        // Act
        AdjacencyListGraph<String> minimumSpanningTree = graph.kruskalAL();

        // Assert
        assertNotNull(minimumSpanningTree);
        assertEquals(0, minimumSpanningTree.getVertices().size()); // Empty graph should result in an MST with no vertices
        assertEquals(0, countEdges(minimumSpanningTree)); // Empty graph should result in an MST with no edges
    }

    @Test
    public void testKruskalInterestingCase() {
        AdjacencyListGraph<Character> graph = new AdjacencyListGraph<>();
        Vertex<Character> vertexA = new Vertex<>('A');
        Vertex<Character> vertexB = new Vertex<>('B');
        Vertex<Character> vertexC = new Vertex<>('C');
        Vertex<Character> vertexD = new Vertex<>('D');

        graph.addVertex(vertexA);
        graph.addVertex(vertexB);
        graph.addVertex(vertexC);
        graph.addVertex(vertexD);

        graph.addEdge(vertexA, vertexB, 1);
        graph.addEdge(vertexA, vertexC, 5);
        graph.addEdge(vertexB, vertexD, 2);
        graph.addEdge(vertexC, vertexD, 1);

        AdjacencyListGraph<Character> mstGraph = graph.kruskalAL();

        assertNotNull(mstGraph);
        assertEquals(4, mstGraph.getVertices().size());
        assertEquals(3, countEdges(mstGraph));
    }

}

