import random
import os

input_dir = "/Users/antonizmujdzin/Coding/Java/team-ninja-sigmy-na-jimp/input"
os.makedirs(input_dir, exist_ok=True)

# Graph 1: Complex graph with high-degree nodes (dense components connected by bridges)
with open(os.path.join(input_dir, "complex_dense.txt"), "w") as f:
    edge_idx = 1
    # 3 clusters of 20 nodes each
    clusters = [range(0, 20), range(20, 40), range(40, 60)]
    for cluster in clusters:
        for i in cluster:
            # Each node connects to 6 other random nodes in the same cluster
            targets = random.sample(cluster, 6)
            for t in targets:
                if i < t:  # Avoid duplicate undirected edges in generation logic if we want, but it's fine
                    f.write(f"e{edge_idx} {i} {t} 10.0\n")
                    edge_idx += 1
    
    # Connect clusters (bridges)
    f.write(f"e{edge_idx} 0 20 15.0\n"); edge_idx += 1
    f.write(f"e{edge_idx} 20 40 15.0\n"); edge_idx += 1
    f.write(f"e{edge_idx} 40 0 15.0\n"); edge_idx += 1
    
    # Add a central hub connected to many nodes (degree 15)
    hub = 60
    for t in random.sample(range(0, 60), 15):
        f.write(f"e{edge_idx} {hub} {t} 20.0\n")
        edge_idx += 1

# Graph 2: Large star/tree-like graph with long branches (to test "gałąź która ma min 5 krawędzi")
with open(os.path.join(input_dir, "long_branches.txt"), "w") as f:
    edge_idx = 1
    # Central node
    central = 0
    node_idx = 1
    # Create 10 branches
    for branch in range(10):
        # Each branch is a path of length 6 to 10
        path_len = random.randint(6, 10)
        prev = central
        for step in range(path_len):
            f.write(f"e{edge_idx} {prev} {node_idx} 10.0\n")
            edge_idx += 1
            prev = node_idx
            node_idx += 1
            
        # Add some sub-branches to make it more complex
        sub_prev = node_idx - 3
        for step in range(4):
            f.write(f"e{edge_idx} {sub_prev} {node_idx} 8.0\n")
            edge_idx += 1
            sub_prev = node_idx
            node_idx += 1

print("Generated complex_dense.txt and long_branches.txt")
