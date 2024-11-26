import { useNavigate } from "react-router-dom";
import { Box, Flex, Icon, Text } from "@chakra-ui/react";
import { useContext } from "react";
import { AuthenticationContext } from "../context/AuthenticationProvider.jsx";
import { HiOutlinePencilSquare } from "react-icons/hi2";

function NavbarItem(props) {
  return (
    <Box
      css={{
        paddingX: "15px",
        paddingY: "10px",
      }}
      _hover={{
        bgColor: "gray.300",
        cursor: "pointer",
      }}
      {...props}
    >
      {props.children}
    </Box>
  );
}

export function Navbar() {
  const navigate = useNavigate();

  // step 2 : context 사용하기
  const { id, isAdmin, isAuthenticated, logout } = useContext(
    AuthenticationContext,
  );

  return (
    <Flex gap={3} mb={6}>
      <NavbarItem onClick={() => navigate("/")}>홈</NavbarItem>
      {isAuthenticated && (
        <NavbarItem onClick={() => navigate("/add")}>
          <Icon hideFrom={"md"}>
            <HiOutlinePencilSquare />
          </Icon>
          <Text hideBelow={"md"}>작성</Text>
        </NavbarItem>
      )}
      <Box mx={"auto"}></Box>
      {isAuthenticated || (
        <NavbarItem onClick={() => navigate("/member/signup")}>가입</NavbarItem>
      )}
      {isAdmin && (
        <NavbarItem onClick={() => navigate("/member/list")}>
          회원목록
        </NavbarItem>
      )}
      {isAuthenticated || (
        <NavbarItem onClick={() => navigate("/member/login")}>
          로그인
        </NavbarItem>
      )}
      {isAuthenticated && (
        <NavbarItem
          onClick={() => {
            logout();
            navigate("/member/login");
          }}
        >
          로그아웃
        </NavbarItem>
      )}
      <NavbarItem onClick={() => navigate(`/member/${id}`)}>{id}</NavbarItem>
    </Flex>
  );
}
