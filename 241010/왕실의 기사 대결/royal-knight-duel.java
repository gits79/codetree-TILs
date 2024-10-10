import java.io.*;
import java.util.*;

/*
문제 정의

왕실의 기사들은 L * L크기의 체스판 위에서 대결을 준비한다
체스판의 왼쪽 상단은 (1,1) 으로 시작하며, 각 칸은 빈칸, 함정 또는 벽으로 구성된다
체스판 밖도 벽으로 간주한다
왕실의 기사들은 자신의 마력으로 상대방을 미쳐낼 수 있다
각 기사의 초기위치는 (r, c)로 주어지며, 그들은 방패를 들고 있기문에
(r, c)를 좌측 상단으로하며 h * w 크기의 직사각형 형태를 띈다
각 기사의 체력은 k로 주어진다

(1) 기사 이동
왕에게 명령을 받은 기사는 상하좌우 중 하나로 한 칸 이동할 수 있다
이때 만약 이동하려는 위치에 다른 기사가 있다면 그 기사도 함께 연쇄적으로 한 칸 밀려남
그 옆에 또 기사가 있다면 연쇄적으로 한 칸씩 밀림
하지만 만약 기사가 이동하려는 방향의 끝에 벽이 있다면 모든 기사는 이동 할 수 없음
또 체스판에서 사라진 기사에게 명령을 내리면 아무런 반응이 없다

(2) 대결 데미지
명령을 받은 기사가 다른 기사를 밀치게 되면, 밀려난 기사들은 피해를 입게 된다
이때 각 기사들은 해당 기사가 이동한 곳에서 w * h 직사각형 내에 놓여 있는 함정의 수
만큼만 피해를 입게 된다. 각 기사마다 피해를 받은 만큼 체력이 깎이며
현재 체력 이상의 데미지를 받을 경우 기사는 체스판에서 사라진다
단 명령을 받은 기사는 피해를 입지 않으며 기사들은 모두 밀린 이후에 데미지를 입는다
밀렸더라도 밀쳐진 위치에 함정이 없다면 그 기사는 피해를 입지 않는다

Q번에 걸쳐 왕의 명령이 주어졌을 떄, Q번의 대결이 모두 끝난 후 생존한 기사들이 총 받은 데미지의
합을 출력하시오 */

/*
메서드 구현

1. 기사들의 정보
1-1) 기사들의 위치와 넓이만 가지는 새로운 L * L 배열 생성
1-2) 배열의 초기값은 0이고 기사들이 차지하는 공간은 각 기사들의 번호를 넣음
1-3) 기사들의 체력만을 가지고 있는 일차원 배열을 생성


2. 이동 구현
2-1) 이동 방향으로 한 칸 이동할때 벽인 경우 -> 이동 불가
2-2) 벽을 체크하는 메서드  인덱스 범위를 초과하는지 or 해당 칸이 벽인지
추가적메서드로 불린 값으로 이동 여부 판단
2-3) 이동가능하며 기사가 없는 경우 -> 해당 칸으로 이동
단순 인덱스 이동
2-4) 이동가능하지만 기사가 있는 경우 -> 이동한칸의 기사를 이동시킴
2-4-1) 이동가능한 기사들의 번호를 가지는 어레이리스트 생성
ni, nj를 통해서 기사를 만난다면 어레이리스트에 만난 기사의 번호를 넣음(처음 민 애도 넣기)
2-5) 벽을 만난다면 이동 불가
벽을 만난다면 false 후 어레이리스트 초기화
2-6) 이동하는 기사들이 모두 벽을 만나지 않으면 이동
이동이가능하다면 어레이리스트의 반대부터 해당 방향으로 한칸씩 이동

3. 대결 데미지 구현
3-1) 밀쳐서 이동을 했다면 map 배열에서 함정의 위치를 불러옴
3-2) 이동한 어레이리스트에서 해당 기사들을 꺼내 함정과 비교
밀친애는 리스트의 첫번째 이므로 빼고 계산
3-3) 체력이 0이 되면 해당 기사의 위치는 모두 0으로 초기화
 */

public class Main {
	private static int L, N, Q, map[][], knight[][];
	private static List<Integer> canGo = new ArrayList<>();
	private static int[] hp; // 상 우 하 좌
	private static int[] hp_first; // 상 우 하 좌
	private static final int[] di = { -1, 0, 1, 0 };
	private static final int[] dj = { 0, 1, 0, -1 };

	// 2-1) 이동 방향으로 한 칸 이동할때 벽인 경우 -> 이동 불가
	// 인덱스 벗어나도 벽, 벽이 있는지
	private static boolean isWall(int i, int j, int dir) {
		int ni = i + di[dir];
		int nj = j + dj[dir];
		if (0 > ni || ni >= L || 0 > nj || nj >= L || map[ni][nj] == 2) {
			return true;
		}
		return false;
	}

	// 2-3) 이동가능하며 기사가 없는 경우 -> 해당 칸으로 이동
	// 해당 칸으로 이동했을 때 기사기 있다면 해당 기사 번호 리턴
	// 아니면 자기 자신 리턴
	private static int isKnight(int i, int j, int num, int dir) {
		int ni = i + di[dir];
		int nj = j + dj[dir];
		if (knight[ni][nj] != num && knight[ni][nj] != 0) {
			return knight[ni][nj];
		}
		return num;
	}

	// 2. 이동 구현
//2-1) 이동 방향으로 한 칸 이동할때 벽인 경우 -> 이동 불가
//2-2) 벽을 체크하는 메서드  인덱스 범위를 초과하는지 or 해당 칸이 벽인지
//    추가적메서드로 불린 값으로 이동 여부 판단
	// 2-3) 이동가능하며 기사가 없는 경우 -> 해당 칸으로 이동
//    단순 인덱스 이동
//2-4) 이동가능하지만 기사가 있는 경우 -> 이동한칸의 기사를 이동시킴
//2-4-1) 이동가능한 기사들의 번호를 가지는 어레이리스트 생성
	// ni, nj를 통해서 기사를 만난다면 어레이리스트에 만난 기사의 번호를 넣음(처음 민 애도 넣기)
//2-5) 벽을 만난다면 이동 불가
//    벽을 만난다면 false 후 어레이리스트 초기화
//2-6) 이동하는 기사들이 모두 벽을 만나지 않으면 이동
//    이동이가능하다면 어레이리스트의 반대부터 해당 방향으로 한칸씩 이동
	/*
	 * 현재 기사에 대해 다음 기사를 탐색 자신음 포함한 이동하는 기사들을 리스트에 넣음 이동하는 기사가 벽을 만난다면 리스트는 초기화 후 끝
	 * 이동하는 기사가 이동을 한다면 리스트 추가후 끝 이제 뒤에서부터 이동 이동하는 기사가 기사를 만난다면 리스트에 추가
	 */
	private static boolean moveChk(int num, int dir) {
		boolean result = false;
		for (int i = 0; i < L; i++) {
			for (int j = 0; j < L; j++) {
				// num에 대한 기사에 대해 이동 가능 여부 판단
				if (knight[i][j] == num) {
					// 이동할 경우 벽은 만난다면 이동 불가
					if (isWall(i, j, dir)) {
						canGo.clear();
						return false;
//                        break;
					}
					// 벽을 만나지 않는경우
					else {
						// 기사를 만나지 않는 경우
						if (isKnight(i, j, num, dir) == num) {
							result = true;
						}
						// 기사를 만난 경우
						else {
							if(!canGo.contains(isKnight(i, j, num, dir)))
								canGo.add(isKnight(i, j, num, dir));
							moveChk(isKnight(i, j, num, dir), dir);
						}
					}
				}
			}

		}
		return result;
	}

	private static void go(int start, int dir) {
		canGo.add(0, start);

//		System.out.println(canGo);
		for (int l = canGo.size() - 1; l >= 0; l--) {
			int num_knight = canGo.get(l);

			temp(dir, num_knight);
		}
//		System.out.println("damage전");
//		for (int[] a : knight)
//			System.out.println(Arrays.toString(a));
//		System.out.println();
		damage(start);
		chkHp();
//		System.out.println("damage후");
//		for (int[] a : knight)
//			System.out.println(Arrays.toString(a));
//		System.out.println();
//		System.out.println(Arrays.toString(hp_first));
//		System.out.println(Arrays.toString(hp));
		canGo.clear();

	}

	private static void damage(int start) {
		for (int i = 0; i < L; i++) {
			for (int j = 0; j < L; j++) {
				if (map[i][j] == 1) {
					if (knight[i][j] == start)
						continue;
					for(int a : canGo) {
						if(knight[i][j] == a) {
//							System.out.println("cango a:" + a);
							hp[knight[i][j]] -= 1;
						}
					}
				}
			}
		}
	}

	private static void delete(int num_knight) {
		for (int i = 0; i < L; i++) {
			for (int j = 0; j < L; j++) {
				if (knight[i][j] == num_knight)
					knight[i][j] = 0;
			}
		}
	}

	private static void chkHp() {
		for (int i = 1; i < N + 1; i++) {
			if (hp[i] <= 0)
				delete(i);
		}
	}

	private static void temp(int dir, int num_knight) {
		if (dir == 1) {

			boolean[][] v = new boolean[L][L];
			for (int i = 0; i < L; i++) {
				for (int j = 0; j < L; j++) {
					if (knight[i][j] == num_knight) {
						int ni = i + di[dir];
						int nj = j + dj[dir];
						// 종료 조건 다음이 자신이 아닐때

						if (knight[ni][nj] != num_knight) {
							knight[ni][nj] = num_knight;
							if (!v[i][j])
								knight[i][j] = 0;
							break;
						}
						// 다음이 자신일때
						else {
							v[ni][nj] = true;
							knight[i][j] = 0;
						}

					}

				}
			}
		} else if (dir == 3) {
			boolean[][] v = new boolean[L][L];
			for (int i = 0; i < L; i++) {
				for (int j = L - 1; j >= 0; j--) {
					if (knight[i][j] == num_knight) {
						int ni = i + di[dir];
						int nj = j + dj[dir];

						// 종료 조건 다음이 자신이 아닐때

						if (knight[ni][nj] != num_knight) {
							knight[ni][nj] = num_knight;
							if (!v[i][j])
								knight[i][j] = 0;
							break;
						}
						// 다음이 자신일때
						else {
							v[ni][nj] = true;
							knight[i][j] = 0;
						}

					}

				}
			}
		}
		
		else if (dir == 0) {
			boolean[][] v = new boolean[L][L];
			for (int i = 0; i < L; i++) {
				for (int j = L - 1; j >= 0; j--) {
					if (knight[i][j] == num_knight) {
						int ni = i + di[dir];
						int nj = j + dj[dir];
						// 종료 조건 다음이 자신이 아닐때

						if (knight[ni][nj] != num_knight) {
							knight[ni][nj] = num_knight;
							if (!v[i][j])
								knight[i][j] = 0;
							break;
						}
						// 다음이 자신일때
						else {
							v[ni][nj] = true;
							knight[i][j] = 0;
						}

					}

				}
			}
		}
		
		else if (dir == 2) {
			boolean[][] v = new boolean[L][L];
			for (int i = L - 1; i >= 0; i++) {
				for (int j = L - 1; j >= 0; j--) {
					if (knight[i][j] == num_knight) {
						int ni = i + di[dir];
						int nj = j + dj[dir];
						// 종료 조건 다음이 자신이 아닐때

						if (knight[ni][nj] != num_knight) {
							knight[ni][nj] = num_knight;
							if (!v[i][j])
								knight[i][j] = 0;
							break;
						}
						// 다음이 자신일때
						else {
							v[ni][nj] = true;
							knight[i][j] = 0;
						}

					}

				}
			}
		}
		
	}

//2. 이동 구현
//2-1) 이동 방향으로 한 칸 이동할때 벽인 경우 -> 이동 불가
//2-2) 벽을 체크하는 메서드  인덱스 범위를 초과하는지 or 해당 칸이 벽인지
//    추가적메서드로 불린 값으로 이동 여부 판단

	public static void main(String[] args) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		L = Integer.parseInt(st.nextToken());
		N = Integer.parseInt(st.nextToken());
		Q = Integer.parseInt(st.nextToken());
		map = new int[L][L];
		knight = new int[L][L];
		hp = new int[N + 1];
		hp_first = new int[N + 1];

		// map 입력 받기
		for (int i = 0; i < L; i++) {
			st = new StringTokenizer(br.readLine());
			for (int j = 0; j < L; j++) {
				map[i][j] = Integer.parseInt(st.nextToken());
			}
		}

		// 기사 위치 입력받기
//        1. 기사들의 정보
//        1-1) 기사들의 위치와 넓이만 가지는 새로운 L * L 배열 생성
//        1-2) 배열의 초기값은 0이고 기사들이 차지하는 공간은 각 기사들의 번호를 넣음
//        1-3) 기사들의 체력만을 가지고 있는 일차원 배열을 생성
		for (int i = 0; i < N; i++) {
			st = new StringTokenizer(br.readLine());

			int r = Integer.parseInt(st.nextToken());
			int c = Integer.parseInt(st.nextToken());
			int h = Integer.parseInt(st.nextToken());
			int w = Integer.parseInt(st.nextToken());
			int k = Integer.parseInt(st.nextToken());
			hp[i + 1] = k;
			hp_first[i + 1] = k;
			for (int x = r - 1; x < r + h - 1; x++) {
				for (int y = c - 1; y < c + w - 1; y++) {
					// i + 1 번 기사들이 차지하는 공간
					knight[x][y] = i + 1;
				}
			}

		}

		// 명령 입력 받기
		for (int i = 0; i < Q; i++) {
			st = new StringTokenizer(br.readLine());
			int n = Integer.parseInt(st.nextToken());
			int dir = Integer.parseInt(st.nextToken());
			moveChk(n, dir);
			if (canGo.size() > 0)
				go(n, dir);

//                for (int[] a : knight) System.out.println(Arrays.toString(a));
//                System.out.println();
//            }
		}
//        for (int[] a : knight) System.out.println(Arrays.toString(a));
		int answer = 0;
		for (int i = 1; i < N + 1; i++) {
			if (hp[i] > 0)
				answer += hp_first[i] - hp[i];
		}

//        System.out.println(Arrays.toString(hp));
		System.out.println(answer);

//        System.out.println(moveChk(1,2));
//        System.out.println(canGo.toString());
//        System.out.println(moveChk(2,1));
//        System.out.println(canGo.toString());
//        System.out.println(moveChk(3,3));
//        System.out.println(canGo.toString());
//        for (int[] a : map) System.out.println(Arrays.toString(a));
//        System.out.println();
//        for (int[] a : knight) System.out.println(Arrays.toString(a));

	}
}