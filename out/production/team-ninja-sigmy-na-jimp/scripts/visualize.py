import sys
import matplotlib.pyplot as plt

def main():
    if len(sys.argv) < 3:
        print("Usage: python3 visualize.py <in_file.txt> <out_file.txt>")
        return

    in_file = sys.argv[1]
    if '/' not in in_file:
        in_file = f"input/{in_file}"
        
    out_file = sys.argv[2]
    if '/' not in out_file:
        out_file = f"output/{out_file}"

    # Read edges
    edges = []
    try:
        with open(in_file, 'r') as f:
            for line in f:
                parts = line.strip().split()
                if len(parts) == 4:
                    _, u, v, _ = parts
                    edges.append((int(u), int(v)))
    except Exception as e:
        print(f"Error reading {in_file}: {e}")
        return

    # Read nodes
    nodes = {}
    try:
        with open(out_file, 'r') as f:
            for line in f:
                parts = line.strip().split()
                if len(parts) >= 3:
                    node_id, x, y = parts[0], parts[1], parts[2]
                    nodes[int(node_id)] = (float(x), float(y))
    except Exception as e:
        print(f"Error reading {out_file}: {e}")
        return

    # Plotting
    plt.figure(figsize=(10, 8))

    # Draw edges
    for u, v in edges:
        if u in nodes and v in nodes:
            x_values = [nodes[u][0], nodes[v][0]]
            y_values = [nodes[u][1], nodes[v][1]]
            plt.plot(x_values, y_values, 'gray', zorder=1, linewidth=1.5)

    # Draw nodes
    for node_id, (x, y) in nodes.items():
        if node_id == 0 and not any(u == 0 or v == 0 for u, v in edges):
            continue # Skip unused 0 node
            
        plt.scatter(x, y, s=300, c='skyblue', edgecolors='navy', zorder=2)
        plt.text(x, y, str(node_id), fontsize=8, ha='center', va='center', color='black', zorder=3, fontweight='bold')

    plt.title("Wizualizacja Algorytmu")
    plt.xlabel("X")
    plt.ylabel("Y")
    plt.grid(True, linestyle='--', alpha=0.6)
    
    output_png = "graph_visualization.png"
    plt.savefig(output_png, bbox_inches='tight', dpi=300)
    print(f"Visualization saved to {output_png}")

if __name__ == "__main__":
    main()
