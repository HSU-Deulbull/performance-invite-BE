# 개발 순서
1. issue 만들기
2. issue 번호를 참고해 branch 만들기
- (주의) **develop branch에서 git pull 을 한 후**, 새로운 branch를 만들어야 함
- branch 이름은 '본인이름/#이슈번호' 사용
3. 새로 만든 branch에서 작업 진행
4. git add, git commit(아래 convention 참고) 후 develop branch에 PR 날리기
5. test 통과 시 merge
6. gitignore로 처리된 설정파일 등이 변경되었다면 팀원에게 공유하기
7. 다시 1로 돌아가 작업하기

<br>


# commit convention
Angular Commit Message Convention 사용
- type(scope): subject
- 참고(https://gist.github.com/stephenparish/9941e89d80e2bc58a153)

