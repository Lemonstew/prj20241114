import { Box, Input, Stack } from "@chakra-ui/react";
import { Field } from "../../components/ui/field.jsx";
import { Button } from "../../components/ui/button.jsx";
import { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { toaster } from "../../components/ui/toaster.jsx";

export function MemberLogin() {
  const [id, setId] = useState("");
  const [password, setPassword] = useState("");
  const navigate = useNavigate();

  function handleLoginClick() {
    axios
      .post("/api/member/login", { id, password })
      .then((res) => res.data)
      .then((data) => {
        // 토스트 띄우고
        toaster.create({
          type: data.message.type,
          description: data.message.text,
        });
        // "/"로 이동
        navigate("/");
        // localStorage 에 token 저장
        localStorage.setItem("token", data.token);
      })
      .catch((e) => {
        const message = e.response.data.message;
        // 토스트 띄우고
        toaster.create({
          type: e.message.type,
          description: e.message.text,
        });
      })
      .finally();
  }

  return (
    <Box>
      <h3>로그인</h3>
      <Stack>
        <Field label={"아이디"}>
          <Input value={id} onChange={(e) => setId(e.target.value)} />
        </Field>
        <Field label={"암호"}>
          <Input
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
        </Field>
        <Box>
          <Button onClick={handleLoginClick}>로그인</Button>
        </Box>
      </Stack>
    </Box>
  );
}
