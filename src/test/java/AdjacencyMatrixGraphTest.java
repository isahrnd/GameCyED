import com.example.pipegame.model.AdjacencyListGraph;
import com.example.pipegame.model.AdjacencyMatrixGraph;
import com.example.pipegame.model.Edge;
import com.example.pipegame.model.Vertex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class AdjacencyMatrixGraphTest {

    private AdjacencyMatrixGraph<Integer> graph;

    @BeforeEach
    public void setUp() {
        graph = new AdjacencyMatrixGraph<>();
    }

    @Test
    public void testAddVertexStandard() {
        AdjacencyMatrixGraph<Integer> graph = new AdjacencyMatrixGraph<>();
        Vertex<Integer> vertexA = new Vertex<>(1);

        graph.addVertex(vertexA);

        assertNotNull(graph.findVertex(1));
        assertEquals(vertexA, graph.findVertex(1));
    }

    @Test
    public void testAddVertexEdgeCases() {
        AdjacencyMatrixGraph<Integer> graph = new AdjacencyMatrixGraph<>();
        // Añadir muchos vértices
        for (int i = 0; i < 1000; i++) {
            Vertex<Integer> vertex = new Vertex<>(i);
            graph.addVertex(vertex);
            assertNotNull(graph.findVertex(i));
            assertEquals(vertex, graph.findVertex(i));
        }
    }

    @Test
    public void testAddVertexInteresting() {
        AdjacencyMatrixGraph<String> graph = new AdjacencyMatrixGraph<>();
        Vertex<String> vertexA = new Vertex<>("A");
        Vertex<String> vertexB = new Vertex<>("B");
        Vertex<String> vertexC = new Vertex<>("C");

        graph.addVertex(vertexA);
        graph.addVertex(vertexB);
        graph.addVertex(vertexC);

        assertNotNull(graph.findVertex("A"));
        assertNotNull(graph.findVertex("B"));
        assertNotNull(graph.findVertex("C"));
    }

    @Test
    public void testFindVertexStandard() {
        AdjacencyMatrixGraph<String> graph = new AdjacencyMatrixGraph<>();
        Vertex<String> vertexA = new Vertex<>("A");

        graph.addVertex(vertexA);

        assertNotNull(graph.findVertex("A"));
        assertEquals(vertexA, graph.findVertex("A"));
    }

    @Test
    public void testFindVertexEdgeCases() {
        AdjacencyMatrixGraph<Integer> graph = new AdjacencyMatrixGraph<>();
        assertNull(graph.findVertex(0)); // Grafo vacío

        Vertex<Integer> vertex = new Vertex<>(1);
        graph.addVertex(vertex);

        assertNull(graph.findVertex(2)); // Vértice no existente
    }

    @Test
    public void testFindVertexInteresting() {
        AdjacencyMatrixGraph<Character> graph = new AdjacencyMatrixGraph<>();
        Vertex<Character> vertexA = new Vertex<>('A');
        Vertex<Character> vertexB = new Vertex<>('B');
        graph.addVertex(vertexA);
        graph.addVertex(vertexB);

        assertNotNull(graph.findVertex('A'));
        assertNotNull(graph.findVertex('B'));

        assertEquals(vertexA, graph.findVertex('A'));
        assertNotEquals(vertexB, graph.findVertex('A'));
    }

    @Test
    public void testRemoveVertexStandard() {
        AdjacencyMatrixGraph<Integer> graph = new AdjacencyMatrixGraph<>();
        Vertex<Integer> vertexA = new Vertex<>(1);

        graph.addVertex(vertexA);
        graph.removeVertex(vertexA);

        assertNull(graph.findVertex(1));
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
        AdjacencyMatrixGraph<String> graph = new AdjacencyMatrixGraph<>();
        Vertex<String> vertexA = new Vertex<>("A");
        Vertex<String> vertexB = new Vertex<>("B");
        Vertex<String> vertexC = new Vertex<>("C");

        graph.addVertex(vertexA);
        graph.addVertex(vertexB);
        graph.addVertex(vertexC);

        graph.removeVertex(vertexB);

        assertNotNull(graph.findVertex("A"));
        assertNull(graph.findVertex("B"));
        assertNotNull(graph.findVertex("C"));
    }

    @Test
    public void testAddEdgeStandard() {
        AdjacencyMatrixGraph<String> graph = new AdjacencyMatrixGraph<>();
        Vertex<String> vertexA = new Vertex<>("A");
        Vertex<String> vertexB = new Vertex<>("B");

        graph.addVertex(vertexA);
        graph.addVertex(vertexB);
        graph.addEdge(vertexA, vertexB, 5);

        Edge<String> edge = graph.findEdge(vertexA, vertexB);
        assertNotNull(edge);
        assertEquals(5, edge.getWeight());
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
        AdjacencyMatrixGraph<Character> graph = new AdjacencyMatrixGraph<>();
        Vertex<Character> vertexA = new Vertex<>('A');
        Vertex<Character> vertexB = new Vertex<>('B');
        Vertex<Character> vertexC = new Vertex<>('C');

        graph.addVertex(vertexA);
        graph.addVertex(vertexB);
        graph.addVertex(vertexC);
        graph.addEdge(vertexA, vertexB, 2);
        graph.addEdge(vertexB, vertexC, 4);

        Edge<Character> edge1 = graph.findEdge(vertexA, vertexB);
        Edge<Character> edge2 = graph.findEdge(vertexB, vertexC);

        assertNotNull(edge1);
        assertNotNull(edge2);
        assertEquals(2, edge1.getWeight());
        assertEquals(4, edge2.getWeight());
    }

    @Test
    public void testRemoveEdgeStandard() {
        AdjacencyMatrixGraph<String> graph = new AdjacencyMatrixGraph<>();
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
        AdjacencyMatrixGraph<Integer> graph = new AdjacencyMatrixGraph<>();
        Vertex<Integer> vertexA = new Vertex<>(1);
        Vertex<Integer> vertexB = new Vertex<>(2);

        graph.addVertex(vertexA);
        graph.addVertex(vertexB);
        graph.removeEdge(vertexA, vertexB); // Eliminar un borde en un grafo sin bordes
    }

    @Test
    public void testRemoveEdgeInteresting() {
        AdjacencyMatrixGraph<String> graph = new AdjacencyMatrixGraph<>();
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
        AdjacencyMatrixGraph<Integer> graph = new AdjacencyMatrixGraph<>();
        Vertex<Integer> vertexA = new Vertex<>(1);
        Vertex<Integer> vertexB = new Vertex<>(2);
        Vertex<Integer> vertexC = new Vertex<>(3);

        graph.addVertex(vertexA);
        graph.addVertex(vertexB);
        graph.addVertex(vertexC);
        graph.addEdge(vertexA, vertexB, 1);
        graph.addEdge(vertexB, vertexC, 2);
        graph.addEdge(vertexA, vertexC, 3);

        ArrayList<Vertex<Integer>> dfsOrder = graph.dfs(vertexA);

        assertNotNull(dfsOrder);
        assertEquals(3, dfsOrder.size());
    }

    @Test
    public void testDFSEdgeCases() {
        AdjacencyMatrixGraph<Integer> graph = new AdjacencyMatrixGraph<>();
        Vertex<Integer> vertexA = new Vertex<>(1);

        graph.addVertex(vertexA);
        ArrayList<Vertex<Integer>> dfsOrder = graph.dfs(vertexA);

        assertNotNull(dfsOrder);
        assertEquals(1, dfsOrder.size());
        assertEquals(vertexA, dfsOrder.get(0));
    }

    @Test
    public void testDFSInteresting() {
        AdjacencyMatrixGraph<Character> graph = new AdjacencyMatrixGraph<>();
        Vertex<Character> vertexA = new Vertex<>('A');
        Vertex<Character> vertexB = new Vertex<>('B');
        Vertex<Character> vertexC = new Vertex<>('C');
        Vertex<Character> vertexD = new Vertex<>('D');

        graph.addVertex(vertexA);
        graph.addVertex(vertexB);
        graph.addVertex(vertexC);
        graph.addVertex(vertexD);

        graph.addEdge(vertexA, vertexB, 1);
        graph.addEdge(vertexA, vertexC, 1);
        graph.addEdge(vertexB, vertexD, 1);
        graph.addEdge(vertexC, vertexD, 1);

        ArrayList<Vertex<Character>> dfsOrder = graph.dfs(vertexA);

        assertNotNull(dfsOrder);
        assertEquals(4, dfsOrder.size());
    }

    @Test
    public void testBFSStandard() {
        AdjacencyMatrixGraph<String> graph = new AdjacencyMatrixGraph<>();
        Vertex<String> vertexA = new Vertex<>("A");
        Vertex<String> vertexB = new Vertex<>("B");
        Vertex<String> vertexC = new Vertex<>("C");
        Vertex<String> vertexD = new Vertex<>("D");
        Vertex<String> vertexE = new Vertex<>("E");

        graph.addVertex(vertexA);
        graph.addVertex(vertexB);
        graph.addVertex(vertexC);
        graph.addVertex(vertexD);
        graph.addVertex(vertexE);

        graph.addEdge(vertexA, vertexB, 1);
        graph.addEdge(vertexA, vertexC, 1);
        graph.addEdge(vertexB, vertexD, 1);
        graph.addEdge(vertexC, vertexD, 1);
        graph.addEdge(vertexC, vertexE, 1);

        ArrayList<Vertex<String>> bfsOrder = graph.bfs(vertexA);

        assertNotNull(bfsOrder);
        assertEquals(5, bfsOrder.size()); // En un grafo completamente conectado
        assertEquals(vertexA, bfsOrder.get(0));
        assertEquals(vertexB, bfsOrder.get(1));
        assertEquals(vertexC, bfsOrder.get(2));
        assertEquals(vertexD, bfsOrder.get(3));
        assertEquals(vertexE, bfsOrder.get(4));
    }

    @Test
    public void testBFSEdgeCases() {
        AdjacencyMatrixGraph<Integer> graph = new AdjacencyMatrixGraph<>();
        Vertex<Integer> vertexA = new Vertex<>(1);

        graph.addVertex(vertexA);
        ArrayList<Vertex<Integer>> bfsOrder = graph.bfs(vertexA);

        assertNotNull(bfsOrder);
        assertEquals(1, bfsOrder.size()); // En un grafo con un solo vértice
        assertEquals(vertexA, bfsOrder.get(0));
    }

    @Test
    public void testBFSInteresting() {
        AdjacencyMatrixGraph<Character> graph = new AdjacencyMatrixGraph<>();
        Vertex<Character> vertexA = new Vertex<>('A');
        Vertex<Character> vertexB = new Vertex<>('B');
        Vertex<Character> vertexC = new Vertex<>('C');
        Vertex<Character> vertexD = new Vertex<>('D');
        Vertex<Character> vertexE = new Vertex<>('E');

        graph.addVertex(vertexA);
        graph.addVertex(vertexB);
        graph.addVertex(vertexC);
        graph.addVertex(vertexD);
        graph.addVertex(vertexE);

        graph.addEdge(vertexA, vertexB, 1);
        graph.addEdge(vertexA, vertexC, 1);
        graph.addEdge(vertexB, vertexD, 1);
        graph.addEdge(vertexC, vertexD, 1);
        graph.addEdge(vertexC, vertexE, 1);

        ArrayList<Vertex<Character>> bfsOrder = graph.bfs(vertexA);

        assertNotNull(bfsOrder);
        assertEquals(5, bfsOrder.size()); // En un grafo con múltiples caminos
    }


    @Test
    public void testDijkstraStandardCase() {
        // Arrange
        AdjacencyMatrixGraph<Integer> graph = new AdjacencyMatrixGraph<>();
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
        AdjacencyMatrixGraph<Integer> graph = new AdjacencyMatrixGraph<>();
        Vertex<Integer> vertexA = new Vertex<>(1);

        graph.addVertex(vertexA);
        ArrayList<Vertex<Integer>> shortestPath = graph.dijkstra(vertexA, vertexA);

        assertNotNull(shortestPath);
        assertEquals(1, shortestPath.size()); // El camino desde un vértice hacia sí mismo debería tener solo un vértice
        assertEquals(vertexA, shortestPath.get(0));
    }

    @Test
    public void testDijkstraInteresting() {
        AdjacencyMatrixGraph<Character> graph = new AdjacencyMatrixGraph<>();
        Vertex<Character> vertexA = new Vertex<>('A');
        Vertex<Character> vertexB = new Vertex<>('B');
        Vertex<Character> vertexC = new Vertex<>('C');
        Vertex<Character> vertexD = new Vertex<>('D');

        graph.addVertex(vertexA);
        graph.addVertex(vertexB);
        graph.addVertex(vertexC);
        graph.addVertex(vertexD);

        graph.addEdge(vertexA, vertexB, 5);
        graph.addEdge(vertexA, vertexC, 2);
        graph.addEdge(vertexB, vertexC, 1);
        graph.addEdge(vertexC, vertexD, 4);

        ArrayList<Vertex<Character>> shortestPath = graph.dijkstra(vertexA, vertexD);

        assertNotNull(shortestPath);
        assertEquals(3, shortestPath.size()); // El camino más corto debería tener 3 vértices
    }

    @Test
    public void testFloydWarshallStandard() {
        AdjacencyMatrixGraph<Integer> graph = new AdjacencyMatrixGraph<>();
        Vertex<Integer> vertexA = new Vertex<>(1);
        Vertex<Integer> vertexB = new Vertex<>(2);
        Vertex<Integer> vertexC = new Vertex<>(3);

        graph.addVertex(vertexA);
        graph.addVertex(vertexB);
        graph.addVertex(vertexC);

        graph.addEdge(vertexA, vertexB, 3);
        graph.addEdge(vertexA, vertexC, 8);
        graph.addEdge(vertexB, vertexC, 1);

        int[][] shortestPaths = graph.floydWarshall();

        assertNotNull(shortestPaths);
        assertEquals(3, shortestPaths.length); // El resultado debería tener 3 filas
        assertEquals(3, shortestPaths[0].length); // El resultado debería tener 3 columnas

    }

    @Test
    public void testFloydWarshallEdgeCases() {
        AdjacencyMatrixGraph<String> graph = new AdjacencyMatrixGraph<>();
        int[][] shortestPaths = graph.floydWarshall();

        assertNotNull(shortestPaths);
        assertEquals(0, shortestPaths.length); // En un grafo vacío, la matriz de resultados debería estar vacía
    }

    @Test
    public void testFloydWarshallInteresting() {
        AdjacencyMatrixGraph<Character> graph = new AdjacencyMatrixGraph<>();
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

        int[][] shortestPaths = graph.floydWarshall();

        assertNotNull(shortestPaths);
        assertEquals(4, shortestPaths.length); // Deberían haber 4 nodos
        assertEquals(4, shortestPaths[0].length); // Matriz cuadrada

    }

    @Test
    public void testPrimStandardCase() {
        AdjacencyMatrixGraph<Integer> graph = new AdjacencyMatrixGraph<>();
        Vertex<Integer> vertexA = new Vertex<>(1);
        Vertex<Integer> vertexB = new Vertex<>(2);
        Vertex<Integer> vertexC = new Vertex<>(3);

        graph.addVertex(vertexA);
        graph.addVertex(vertexB);
        graph.addVertex(vertexC);

        graph.addEdge(vertexA, vertexB, 3);
        graph.addEdge(vertexA, vertexC, 8);
        graph.addEdge(vertexB, vertexC, 1);

        AdjacencyMatrixGraph<Integer> mstGraph = graph.primAM();

        assertNotNull(mstGraph);
        assertEquals(3, mstGraph.getVertices().size());
        assertEquals(2, countEdges(mstGraph));
    }

    private <T> int countEdges(AdjacencyMatrixGraph<T> graph) {
        int edgeCount = 0;
        for (int i = 0; i < graph.getVertices().size(); i++) {
            for (int j = i + 1; j < graph.getVertices().size(); j++) {
                if (graph.getAdjacencyMatrix()[i][j] != 0) {
                    // Edge exists between vertices i and j
                    edgeCount++;
                }
            }
        }
        return edgeCount;
    }

    @Test
    public void testPrimEdgeCases() {

        AdjacencyMatrixGraph<Integer> graph = new AdjacencyMatrixGraph<>();
        Vertex<Integer> vertex1 = new Vertex<>(1);
        Vertex<Integer> vertex2 = new Vertex<>(2);

        // Agregar el mínimo número de vértices (dos) y conectarlos
        graph.addVertex(vertex1);
        graph.addVertex(vertex2);
        graph.addEdge(vertex1, vertex2, 1); // Conectar ambos vértices con un peso

        AdjacencyMatrixGraph<Integer> mstGraph = graph.primAM();

        assertNotNull(mstGraph);
        assertEquals(2, mstGraph.getVertices().size());
        assertEquals(1, countEdges(mstGraph)); // El MST tendrá un solo borde en este caso
    }



    @Test
    public void testPrimInterestingCase() {
        AdjacencyMatrixGraph<Character> graph = new AdjacencyMatrixGraph<>();
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

        AdjacencyMatrixGraph<Character> mstGraph = graph.primAM();

        assertNotNull(mstGraph);
        assertEquals(4, mstGraph.getVertices().size()); // MST debería tener 4 nodos
    }



    @Test
    public void testKruskalStandardCase() {
        AdjacencyMatrixGraph<Integer> graph = new AdjacencyMatrixGraph<>();
        Vertex<Integer> vertexA = new Vertex<>(1);
        Vertex<Integer> vertexB = new Vertex<>(2);
        Vertex<Integer> vertexC = new Vertex<>(3);

        graph.addVertex(vertexA);
        graph.addVertex(vertexB);
        graph.addVertex(vertexC);

        graph.addEdge(vertexA, vertexB, 3);
        graph.addEdge(vertexA, vertexC, 8);
        graph.addEdge(vertexB, vertexC, 1);

        AdjacencyMatrixGraph<Integer> mstGraph = graph.kruskalAM();

        assertNotNull(mstGraph);
        assertEquals(3, mstGraph.getVertices().size()); // El grafo MST debería tener los mismos vértices

    }

    @Test
    public void testKruskalEdgeCases() {
        AdjacencyMatrixGraph<String> graph = new AdjacencyMatrixGraph<>();
        AdjacencyMatrixGraph<String> mstGraph = graph.kruskalAM();

        assertNotNull(mstGraph);
        assertEquals(0, mstGraph.getVertices().size()); // El grafo MST debería estar vacío

    }

    @Test
    public void testKruskalInterestingCase() {
        AdjacencyMatrixGraph<Character> graph = new AdjacencyMatrixGraph<>();
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

        AdjacencyMatrixGraph<Character> mstGraph = graph.kruskalAM();

        assertNotNull(mstGraph);
        assertEquals(4, mstGraph.getVertices().size()); // MST debería tener 4 nodos
        assertEquals(3, countEdges(mstGraph));
    }
}
