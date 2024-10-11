import java.io.*;
import java.util.*;
public class Main {
    // 참가자의 위치와 이동 수를 가지는 클래스
    static class Person {
        int x;
        int y;
        int sum;
        boolean isExit = false;
        boolean isRotate = false;
        
        public Person(int x, int y, int sum) {
            super();
            this.x = x;
            this.y = y;
            this.sum = sum;
        }
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("Person [x=").append(x).append(", y=").append(y).append(", sum=").append(sum).append(", isExit=").append(isExit).append("]");
            return builder.toString();
        }
    }
    // 거리를 계산하는 메서드
    private static int dist(int x, int y) {
        return Math.abs(x - exit_x) + Math.abs(y - exit_y);
    }
    // 사방탐색을 통해 벽이 없고 기존 탈출구와의 거리보다 가깝다면 참가자가 이동하는 메서드
    private static void searchAndGo() {
        for (Person p : lst) {
            boolean isExit = p.isExit;
            if (isExit)
                continue;
            int x = p.x;
            int y = p.y;
            int dist = dist(x, y);
            for (int d = 0; d < 4; d++) {
                int nx = x + di[d];
                int ny = y + dj[d];
                if (0 <= nx && nx < N && 0 <= ny && ny < N && map[nx][ny] == 0) {
                    if (dist > dist(nx, ny)) {
                        p.x = nx;
                        p.y = ny;
                        p.sum += 1;
                        if (nx == exit_x && ny == exit_y) {
                            p.isExit = true;
                        }
                        break; // 이동한 경우 더 이상 탐색할 필요 없음
                    }
                }
            }
        }
    }
    // 사각형을 찾아 회전할 범위를 정하는 메서드
    private static int[] searchSquare(Person p) {
        int x = p.x;
        int y = p.y;
        int dist = dist(x, y) + 1;
        int[] ijk = null;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                int cnt = 0;
                for (int r = i; r < i + dist; r++) {
                    for (int c = j; c < j + dist; c++) {
                        if (r == x && c == y)
                            cnt++;
                        else if (r == exit_x && c == exit_y)
                            cnt++;
                    }
                }
                if (cnt == 2)
                    return new int[] { i, j, dist };
            }
        }
        return ijk;
    }
    
    private static void initRotate() {
    	for(Person p : lst) p.isRotate = false;
    }
    
    // 회전하는 메서드
    private static void rotate(int[] ijk) {
        int sr = ijk[0];
        int sc = ijk[1];
        int dist = ijk[2];
        int[][] temp = new int[dist][dist];
        boolean flag = true;
        for (int i = 0; i < dist; i++) {
            for (int j = 0; j < dist; j++) {
                int wall = map[sr + i][sc + j];
                if (wall > 0)
                    wall--;
                // 참가자 위치 회전
                for (Person p : lst) {
                	if(p.isExit) continue;
                    if (p.x == sr + i && p.y == sc + j && !p.isRotate) {
//                    	System.out.println("회전하는 회전 전 person: "+ p);
                        int newX = sr + j;
                        int newY = sc + dist - i -1;  // 회전 후 새로운 x 좌표
                        p.x = newX;
                        p.y = newY;
                        p.isRotate = true;
//                        System.out.println("회전하는 회전 후 person: "+ p);
                    }
                }
                temp[j][dist - i - 1] = wall;  // 벽 회전
                // 출구 위치 회전
                if (sr + i == exit_x && sc + j == exit_y && flag) {
                    exit_x = sr + j;
                    exit_y = sc + dist - i - 1;
                    flag = false;
                }
            }
        }
        // 원래 맵에 업데이트
        update(sr, sc, temp);
        initRotate();
    }
    // 회전 후 맵을 업데이트하는 메서드
    private static void update(int sr, int sc, int[][] temp) {
        int n = temp.length;
        for (int i = sr; i < sr + n; i++) {
            for (int j = sc; j < sc + n; j++) {
                map[i][j] = temp[i - sr][j - sc];
            }
        }
    }
    // BFS로 가장 가까운 참가자를 찾는 메서드
    private static Person bfs() {
        Person person = null;
        ArrayDeque<int[]> q = new ArrayDeque<>();
        boolean[][] v = new boolean[N][N];
        q.offer(new int[] { exit_x, exit_y });
        v[exit_x][exit_y] = true;
        while (!q.isEmpty()) {
            int[] ij = q.poll();
            int i = ij[0];
            int j = ij[1];
            for (int d = 0; d < 4; d++) {
                int ni = i + di[d]; // 수정된 부분: 다음 좌표 계산
                int nj = j + dj[d]; // 수정된 부분: 다음 좌표 계산
                if (0 <= ni && ni < N && 0 <= nj && nj < N && !v[ni][nj]) {
                    v[ni][nj] = true;
                    for (Person p : lst) {
                        if (p.x == ni && p.y == nj && !p.isExit) {
                            return p;  // 가장 가까운 참가자 리턴
                        }
                    }
                    q.offer(new int[] { ni, nj });
                }
            }
        }
        return person;
    }
    static int N, M, K, map[][];
    static int exit_x, exit_y;
    // 상 하 우 좌
    static final int[] di = { -1, 1, 0, 0 };
    static final int[] dj = { 0, 0, 1, -1 };
    static List<Person> lst = new ArrayList<>();
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());
        map = new int[N][N];
        // 맵 입력
        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < N; j++) {
                map[i][j] = Integer.parseInt(st.nextToken());
            }
        }
        // 참가자 입력
        for (int i = 0; i < M; i++) {
            st = new StringTokenizer(br.readLine());
            int r = Integer.parseInt(st.nextToken()) - 1;
            int c = Integer.parseInt(st.nextToken()) - 1;
            lst.add(new Person(r, c, 0));
        }
        // 탈출구 입력
        st = new StringTokenizer(br.readLine());
        exit_x = Integer.parseInt(st.nextToken()) - 1;
        exit_y = Integer.parseInt(st.nextToken()) - 1;
        // K초 동안 진행
        int k = 1;
        while (k <= K) {
        	// System.out.println(k + "번 회전");
        	// System.out.println(k +"이동전 맵");
        	// for(int[] a : map) System.out.println(Arrays.toString(a));
        	// for (Person p : lst) System.out.println(p);
        	
        	
            // 참가자 이동
            searchAndGo();
            
            // System.out.println(k + "이동후 맵");
        	// for(int[] a : map) System.out.println(Arrays.toString(a));
        	// for (Person p : lst) System.out.println(p);
            
            // 가까운 참가자를 찾음
            Person person = bfs(); 
            if (person == null) {
               
                break;  
            }
            // System.out.println("Exit_x : " + exit_x + " , Exit_y: " + exit_y);
            // System.out.println("가장가까운 사람: " + person);
            
            
            int[] ijk = searchSquare(person);
            rotate(ijk);
            k++;
            
            // System.out.println(k + "회전후 맵");
        	// for(int[] a : map) System.out.println(Arrays.toString(a));
        	// for (Person p : lst) System.out.println(p);
        	// System.out.println("Exit_x : " + exit_x + " , Exit_y: " + exit_y);
            // 모든 참가자가 탈출했는지 확인
            boolean isBreak = true;
            for (Person p : lst) {
                if (!p.isExit) {
                    isBreak = false;
                    break;
                }
            }
            if (isBreak) {
                break;
            }
        }
        // System.out.println("k: " + k);
        int answer = 0;
        for (Person p : lst) {
            answer += p.sum;
        }
        System.out.println(answer);
        System.out.println((exit_x + 1) + " " + (exit_y + 1));
    }
}