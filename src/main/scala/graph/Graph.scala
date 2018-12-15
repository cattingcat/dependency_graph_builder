package graph

case class Graph[T, M](nodes: Set[T], edges: Set[Edge[T, M]]) {}
